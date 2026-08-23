package com.smartprocure.modules.procurement.service.impl;

import com.smartprocure.core.exception.BusinessRuleViolationException;
import com.smartprocure.core.exception.ResourceNotFoundException;
import com.smartprocure.core.payload.PagedResponse;
import com.smartprocure.modules.auth.entity.User;
import com.smartprocure.modules.auth.repository.UserRepository;
import com.smartprocure.modules.procurement.dto.PurchaseRequestDTO;
import com.smartprocure.modules.procurement.dto.PurchaseRequestItemDTO;
import com.smartprocure.modules.procurement.entity.PurchaseRequest;
import com.smartprocure.modules.procurement.entity.PurchaseRequestItem;
import com.smartprocure.modules.procurement.enums.PurchaseRequestStatus;
import com.smartprocure.modules.procurement.repository.PurchaseRequestRepository;
import com.smartprocure.modules.procurement.service.PurchaseRequestService;
import com.smartprocure.modules.product.entity.Product;
import com.smartprocure.modules.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service Implementation for Purchase Request lifecycle management.
 *
 * @author Principal Java Architect
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PurchaseRequestServiceImpl implements PurchaseRequestService {

    private final PurchaseRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public PurchaseRequestDTO createPurchaseRequest(String userEmail, PurchaseRequestDTO requestDTO) {
        log.info("Creating purchase requisition for user: {}", userEmail);

        User requester = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        String requestNumber = "PR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        PurchaseRequest request = PurchaseRequest.builder()
                .requestNumber(requestNumber)
                .requestedBy(requester)
                .department(requestDTO.getDepartment())
                .neededByDate(requestDTO.getNeededByDate())
                .status(PurchaseRequestStatus.DRAFT)
                .build();

        BigDecimal totalEstimatedAmount = BigDecimal.ZERO;

        for (PurchaseRequestItemDTO itemDTO : requestDTO.getItems()) {
            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", itemDTO.getProductId()));

            BigDecimal lineTotal = itemDTO.getEstimatedUnitPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity()));
            totalEstimatedAmount = totalEstimatedAmount.add(lineTotal);

            PurchaseRequestItem item = PurchaseRequestItem.builder()
                    .product(product)
                    .quantity(itemDTO.getQuantity())
                    .estimatedUnitPrice(itemDTO.getEstimatedUnitPrice())
                    .totalPrice(lineTotal)
                    .build();

            request.addItem(item);
        }

        request.setTotalEstimatedAmount(totalEstimatedAmount);
        PurchaseRequest savedRequest = requestRepository.save(request);
        log.info("Successfully created Purchase Request ID: {} with number: {}", savedRequest.getId(), savedRequest.getRequestNumber());

        return mapToDTO(savedRequest);
    }

    @Override
    @Transactional
    public PurchaseRequestDTO submitPurchaseRequest(Long id) {
        log.info("Submitting Purchase Request ID: {} for manager approval", id);
        PurchaseRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PurchaseRequest", "id", id));

        if (request.getStatus() != PurchaseRequestStatus.DRAFT) {
            throw new BusinessRuleViolationException("Only DRAFT purchase requests can be submitted.");
        }

        request.setStatus(PurchaseRequestStatus.SUBMITTED);
        PurchaseRequest updated = requestRepository.save(request);
        return mapToDTO(updated);
    }

    @Override
    @Transactional
    public PurchaseRequestDTO approvePurchaseRequest(Long id) {
        log.info("Approving Purchase Request ID: {}", id);
        PurchaseRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PurchaseRequest", "id", id));

        if (request.getStatus() != PurchaseRequestStatus.SUBMITTED) {
            throw new BusinessRuleViolationException("Only SUBMITTED purchase requests can be approved.");
        }

        request.setStatus(PurchaseRequestStatus.APPROVED);
        PurchaseRequest updated = requestRepository.save(request);
        return mapToDTO(updated);
    }

    @Override
    @Transactional
    public PurchaseRequestDTO rejectPurchaseRequest(Long id, String reason) {
        log.info("Rejecting Purchase Request ID: {} with reason: {}", id, reason);
        PurchaseRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PurchaseRequest", "id", id));

        if (request.getStatus() != PurchaseRequestStatus.SUBMITTED) {
            throw new BusinessRuleViolationException("Only SUBMITTED purchase requests can be rejected.");
        }

        request.setStatus(PurchaseRequestStatus.REJECTED);
        request.setRejectionReason(reason);
        PurchaseRequest updated = requestRepository.save(request);
        return mapToDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public PurchaseRequestDTO getPurchaseRequestById(Long id) {
        PurchaseRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PurchaseRequest", "id", id));
        return mapToDTO(request);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<PurchaseRequestDTO> getAllPurchaseRequests(int page, int size, String sortBy, String sortDir, String statusStr) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PurchaseRequest> pageResult;
        if (statusStr != null && !statusStr.trim().isEmpty()) {
            PurchaseRequestStatus status = PurchaseRequestStatus.valueOf(statusStr.toUpperCase());
            pageResult = requestRepository.findByStatus(status, pageable);
        } else {
            pageResult = requestRepository.findAll(pageable);
        }

        List<PurchaseRequestDTO> content = pageResult.getContent().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return PagedResponse.<PurchaseRequestDTO>builder()
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
    public void deletePurchaseRequest(Long id) {
        log.info("Deleting Purchase Request ID: {}", id);
        PurchaseRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PurchaseRequest", "id", id));

        if (request.getStatus() == PurchaseRequestStatus.CONVERTED_TO_PO) {
            throw new BusinessRuleViolationException("Cannot delete a Purchase Request that has already been converted into a Purchase Order.");
        }

        requestRepository.delete(request);
    }

    private PurchaseRequestDTO mapToDTO(PurchaseRequest request) {
        List<PurchaseRequestItemDTO> itemDTOs = request.getItems().stream()
                .map(item -> PurchaseRequestItemDTO.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productSku(item.getProduct().getSku())
                        .productName(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .estimatedUnitPrice(item.getEstimatedUnitPrice())
                        .totalPrice(item.getTotalPrice())
                        .build())
                .collect(Collectors.toList());

        return PurchaseRequestDTO.builder()
                .id(request.getId())
                .requestNumber(request.getRequestNumber())
                .requestedByUserId(request.getRequestedBy().getId())
                .requestedByUserName(request.getRequestedBy().getFirstName() + " " + request.getRequestedBy().getLastName())
                .requestedByUserEmail(request.getRequestedBy().getEmail())
                .department(request.getDepartment())
                .neededByDate(request.getNeededByDate())
                .totalEstimatedAmount(request.getTotalEstimatedAmount())
                .status(request.getStatus())
                .rejectionReason(request.getRejectionReason())
                .items(itemDTOs)
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .build();
    }
}
