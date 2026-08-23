package com.smartprocure.purchase;

import com.smartprocure.common.exception.BusinessRuleException;
import com.smartprocure.common.exception.ResourceNotFoundException;
import com.smartprocure.domain.Product;
import com.smartprocure.domain.PurchaseOrder;
import com.smartprocure.domain.PurchaseOrderItem;
import com.smartprocure.domain.User;
import com.smartprocure.domain.Vendor;
import com.smartprocure.domain.repository.ProductRepository;
import com.smartprocure.domain.repository.PurchaseOrderRepository;
import com.smartprocure.domain.repository.UserRepository;
import com.smartprocure.domain.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final VendorRepository vendorRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<PurchaseOrderDto> getAllPurchaseOrders(Pageable pageable) {
        return purchaseOrderRepository.findAll(pageable).map(this::mapToDto);
    }

    @Transactional
    public PurchaseOrderDto createPurchaseOrder(PurchaseOrderDto dto) {
        Vendor vendor = vendorRepository.findById(dto.getVendorId())
                .orElseThrow(() -> new ResourceNotFoundException("Vendor", "id", dto.getVendorId()));

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User creator = userRepository.findByEmail(username)
                .orElseGet(() -> userRepository.findAll().stream().findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("User", "email", username)));

        String poNumber = "PO-" + System.currentTimeMillis();

        PurchaseOrder po = PurchaseOrder.builder()
                .poNumber(poNumber)
                .vendor(vendor)
                .createdByUser(creator)
                .status("DRAFT")
                .deliveryDate(dto.getDeliveryDate() != null ? dto.getDeliveryDate() : LocalDate.now().plusDays(30))
                .totalAmount(BigDecimal.ZERO)
                .build();

        BigDecimal grandTotal = BigDecimal.ZERO;

        for (PurchaseOrderDto.ItemDto itemDto : dto.getItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", itemDto.getProductId()));

            BigDecimal lineTotal = itemDto.getUnitPrice().multiply(BigDecimal.valueOf(itemDto.getQuantityOrdered()));
            grandTotal = grandTotal.add(lineTotal);

            PurchaseOrderItem item = PurchaseOrderItem.builder()
                    .purchaseOrder(po)
                    .product(product)
                    .quantityOrdered(itemDto.getQuantityOrdered())
                    .quantityReceived(0)
                    .unitPrice(itemDto.getUnitPrice())
                    .totalPrice(lineTotal)
                    .build();

            po.getItems().add(item);
        }

        po.setTotalAmount(grandTotal);
        return mapToDto(purchaseOrderRepository.save(po));
    }

    @Transactional
    public PurchaseOrderDto approvePurchaseOrder(Long id) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PurchaseOrder", "id", id));
        po.setStatus("APPROVED");
        return mapToDto(purchaseOrderRepository.save(po));
    }

    @Transactional
    public PurchaseOrderDto sendToVendor(Long id) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PurchaseOrder", "id", id));
        if (!"APPROVED".equals(po.getStatus())) {
            throw new BusinessRuleException("Only APPROVED purchase orders can be sent to vendor");
        }
        po.setStatus("SENT_TO_VENDOR");
        return mapToDto(purchaseOrderRepository.save(po));
    }

    private PurchaseOrderDto mapToDto(PurchaseOrder po) {
        return PurchaseOrderDto.builder()
                .id(po.getId())
                .poNumber(po.getPoNumber())
                .vendorId(po.getVendor().getId())
                .vendorName(po.getVendor().getCompanyName())
                .status(po.getStatus())
                .totalAmount(po.getTotalAmount())
                .deliveryDate(po.getDeliveryDate())
                .createdByUserName(po.getCreatedByUser() != null ? po.getCreatedByUser().getFirstName() + " " + po.getCreatedByUser().getLastName() : "SYSTEM")
                .build();
    }
}
