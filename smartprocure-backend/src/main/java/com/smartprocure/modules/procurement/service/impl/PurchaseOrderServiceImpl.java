package com.smartprocure.modules.procurement.service.impl;

import com.smartprocure.core.exception.BusinessRuleViolationException;
import com.smartprocure.core.exception.ResourceNotFoundException;
import com.smartprocure.core.payload.PagedResponse;
import com.smartprocure.modules.auth.entity.User;
import com.smartprocure.modules.auth.repository.UserRepository;
import com.smartprocure.modules.procurement.dto.PurchaseOrderDTO;
import com.smartprocure.modules.procurement.dto.PurchaseOrderItemDTO;
import com.smartprocure.modules.procurement.entity.PurchaseOrder;
import com.smartprocure.modules.procurement.entity.PurchaseOrderItem;
import com.smartprocure.modules.procurement.entity.PurchaseRequest;
import com.smartprocure.modules.procurement.entity.PurchaseRequestItem;
import com.smartprocure.modules.procurement.enums.PurchaseOrderStatus;
import com.smartprocure.modules.procurement.enums.PurchaseRequestStatus;
import com.smartprocure.modules.procurement.repository.PurchaseOrderRepository;
import com.smartprocure.modules.procurement.repository.PurchaseRequestRepository;
import com.smartprocure.modules.procurement.service.PurchaseOrderService;
import com.smartprocure.modules.product.entity.Product;
import com.smartprocure.modules.product.repository.ProductRepository;
import com.smartprocure.modules.vendor.entity.Vendor;
import com.smartprocure.modules.vendor.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing Purchase Order Contracts & Vendor Dispatch.
 *
 * @author Principal Java Architect
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private static final BigDecimal DEFAULT_TAX_RATE = new BigDecimal("0.10"); // 10% Standard VAT/Tax Rate

    private final PurchaseOrderRepository orderRepository;
    private final PurchaseRequestRepository requestRepository;
    private final VendorRepository vendorRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public PurchaseOrderDTO createPurchaseOrder(String userEmail, PurchaseOrderDTO orderDTO) {
        log.info("Creating standalone Purchase Order for vendor ID: {}", orderDTO.getVendorId());

        User creator = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        Vendor vendor = vendorRepository.findById(orderDTO.getVendorId())
                .orElseThrow(() -> new ResourceNotFoundException("Vendor", "id", orderDTO.getVendorId()));

        String poNumber = "PO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        PurchaseOrder order = PurchaseOrder.builder()
                .poNumber(poNumber)
                .vendor(vendor)
                .createdByUser(creator)
                .orderDate(LocalDateTime.now())
                .expectedDeliveryDate(orderDTO.getExpectedDeliveryDate())
                .status(PurchaseOrderStatus.DRAFT)
                .build();

        BigDecimal subtotal = BigDecimal.ZERO;

        for (PurchaseOrderItemDTO itemDTO : orderDTO.getItems()) {
            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", itemDTO.getProductId()));

            BigDecimal lineTotal = itemDTO.getUnitPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity()));
            subtotal = subtotal.add(lineTotal);

            PurchaseOrderItem item = PurchaseOrderItem.builder()
                    .product(product)
                    .quantity(itemDTO.getQuantity())
                    .unitPrice(itemDTO.getUnitPrice())
                    .totalPrice(lineTotal)
                    .build();

            order.addItem(item);
        }

        BigDecimal taxAmount = subtotal.multiply(DEFAULT_TAX_RATE);
        BigDecimal grandTotal = subtotal.add(taxAmount);

        order.setTotalAmount(subtotal);
        order.setTaxAmount(taxAmount);
        order.setGrandTotal(grandTotal);

        PurchaseOrder savedOrder = orderRepository.save(order);
        log.info("Successfully created Purchase Order ID: {} with PO Number: {}", savedOrder.getId(), savedOrder.getPoNumber());

        return mapToDTO(savedOrder);
    }

    @Override
    @Transactional
    public PurchaseOrderDTO convertRequestToOrder(Long purchaseRequestId, String userEmail, Long vendorId, LocalDate expectedDeliveryDate) {
        log.info("Converting Purchase Request ID: {} into a formal Purchase Order", purchaseRequestId);

        PurchaseRequest request = requestRepository.findById(purchaseRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("PurchaseRequest", "id", purchaseRequestId));

        if (request.getStatus() != PurchaseRequestStatus.APPROVED) {
            throw new BusinessRuleViolationException("Only APPROVED purchase requests can be converted into a Purchase Order.");
        }

        User creator = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor", "id", vendorId));

        String poNumber = "PO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        PurchaseOrder order = PurchaseOrder.builder()
                .poNumber(poNumber)
                .purchaseRequest(request)
                .vendor(vendor)
                .createdBy(creator)
                .orderDate(LocalDateTime.now())
                .expectedDeliveryDate(expectedDeliveryDate)
                .status(PurchaseOrderStatus.DRAFT)
                .build();

        BigDecimal subtotal = BigDecimal.ZERO;

        for (PurchaseRequestItem reqItem : request.getItems()) {
            BigDecimal lineTotal = reqItem.getEstimatedUnitPrice().multiply(BigDecimal.valueOf(reqItem.getQuantity()));
            subtotal = subtotal.add(lineTotal);

            PurchaseOrderItem orderItem = PurchaseOrderItem.builder()
                    .product(reqItem.getProduct())
                    .quantity(reqItem.getQuantity())
                    .unitPrice(reqItem.getEstimatedUnitPrice())
                    .totalPrice(lineTotal)
                    .build();

            order.addItem(orderItem);
        }

        BigDecimal taxAmount = subtotal.multiply(DEFAULT_TAX_RATE);
        BigDecimal grandTotal = subtotal.add(taxAmount);

        order.setTotalAmount(subtotal);
        order.setTaxAmount(taxAmount);
        order.setGrandTotal(grandTotal);

        request.setStatus(PurchaseRequestStatus.CONVERTED_TO_PO);
        requestRepository.save(request);

        PurchaseOrder savedOrder = orderRepository.save(order);
        log.info("Converted Purchase Request {} into Purchase Order {}", request.getRequestNumber(), savedOrder.getPoNumber());

        return mapToDTO(savedOrder);
    }

    @Override
    @Transactional
    public PurchaseOrderDTO issueOrderToVendor(Long id) {
        log.info("Dispatching Purchase Order ID: {} to vendor", id);
        PurchaseOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PurchaseOrder", "id", id));

        if (order.getStatus() != PurchaseOrderStatus.DRAFT) {
            throw new BusinessRuleViolationException("Only DRAFT purchase orders can be issued to vendors.");
        }

        order.setStatus(PurchaseOrderStatus.ISSUED);
        PurchaseOrder updated = orderRepository.save(order);
        log.info("Purchase Order ID: {} status changed to ISSUED", id);
        return mapToDTO(updated);
    }

    @Override
    @Transactional
    public PurchaseOrderDTO updateOrderStatus(Long id, String statusStr) {
        log.info("Updating Purchase Order ID: {} status to: {}", id, statusStr);
        PurchaseOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PurchaseOrder", "id", id));

        PurchaseOrderStatus newStatus = PurchaseOrderStatus.valueOf(statusStr.toUpperCase());
        order.setStatus(newStatus);

        PurchaseOrder updated = orderRepository.save(order);
        return mapToDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public PurchaseOrderDTO getPurchaseOrderById(Long id) {
        PurchaseOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PurchaseOrder", "id", id));
        return mapToDTO(order);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<PurchaseOrderDTO> getAllPurchaseOrders(int page, int size, String sortBy, String sortDir, String statusStr) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PurchaseOrder> pageResult;
        if (statusStr != null && !statusStr.trim().isEmpty()) {
            PurchaseOrderStatus status = PurchaseOrderStatus.valueOf(statusStr.toUpperCase());
            pageResult = orderRepository.findByStatus(status, pageable);
        } else {
            pageResult = orderRepository.findAll(pageable);
        }

        List<PurchaseOrderDTO> content = pageResult.getContent().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return PagedResponse.<PurchaseOrderDTO>builder()
                .content(content)
                .page(pageResult.getNumber())
                .size(pageResult.getSize())
                .totalElements(pageResult.getTotalElements())
                .totalPages(pageResult.getTotalPages())
                .last(pageResult.isLast())
                .build();
    }

    @Override
    @Transactional
    public void cancelPurchaseOrder(Long id) {
        log.info("Cancelling Purchase Order ID: {}", id);
        PurchaseOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PurchaseOrder", "id", id));

        if (order.getStatus() == PurchaseOrderStatus.COMPLETED) {
            throw new BusinessRuleViolationException("Cannot cancel a COMPLETED Purchase Order.");
        }

        order.setStatus(PurchaseOrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    private PurchaseOrderDTO mapToDTO(PurchaseOrder order) {
        List<PurchaseOrderItemDTO> itemDTOs = order.getItems().stream()
                .map(item -> PurchaseOrderItemDTO.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productSku(item.getProduct().getSku())
                        .productName(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .totalPrice(item.getTotalPrice())
                        .build())
                .collect(Collectors.toList());

        return PurchaseOrderDTO.builder()
                .id(order.getId())
                .poNumber(order.getPoNumber())
                .purchaseRequestId(order.getPurchaseRequest() != null ? order.getPurchaseRequest().getId() : null)
                .purchaseRequestNumber(order.getPurchaseRequest() != null ? order.getPurchaseRequest().getRequestNumber() : null)
                .vendorId(order.getVendor().getId())
                .vendorCode(order.getVendor().getVendorCode())
                .vendorCompanyName(order.getVendor().getCompanyName())
                .createdByUserId(order.getCreatedByUser() != null ? order.getCreatedByUser().getId() : null)
                .createdByUserName(order.getCreatedByUser() != null ? order.getCreatedByUser().getFirstName() + " " + order.getCreatedByUser().getLastName() : order.getCreatedBy())
                .orderDate(order.getOrderDate())
                .expectedDeliveryDate(order.getExpectedDeliveryDate())
                .totalAmount(order.getTotalAmount())
                .taxAmount(order.getTaxAmount())
                .grandTotal(order.getGrandTotal())
                .status(order.getStatus())
                .items(itemDTOs)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}
