package com.smartprocure.purchase;

import com.smartprocure.common.exception.BusinessRuleException;
import com.smartprocure.common.exception.ResourceNotFoundException;
import com.smartprocure.domain.Product;
import com.smartprocure.domain.PurchaseRequest;
import com.smartprocure.domain.PurchaseRequestItem;
import com.smartprocure.domain.User;
import com.smartprocure.domain.repository.ProductRepository;
import com.smartprocure.domain.repository.PurchaseRequestRepository;
import com.smartprocure.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PurchaseRequestService {

    private final PurchaseRequestRepository purchaseRequestRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<PurchaseRequestDto> getAllPurchaseRequests(Pageable pageable) {
        return purchaseRequestRepository.findAll(pageable).map(this::mapToDto);
    }

    @Transactional
    public PurchaseRequestDto createPurchaseRequest(PurchaseRequestDto dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User requester = userRepository.findByEmail(username)
                .orElseGet(() -> userRepository.findAll().stream().findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("User", "email", username)));

        String prNumber = "PR-" + System.currentTimeMillis();

        PurchaseRequest pr = PurchaseRequest.builder()
                .prNumber(prNumber)
                .department(dto.getDepartment())
                .status("DRAFT")
                .totalAmount(BigDecimal.ZERO)
                .requestedBy(requester)
                .build();

        BigDecimal grandTotal = BigDecimal.ZERO;

        for (PurchaseRequestDto.ItemDto itemDto : dto.getItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", itemDto.getProductId()));

            BigDecimal unitPrice = itemDto.getEstimatedUnitPrice() != null ? itemDto.getEstimatedUnitPrice() : product.getUnitPrice();
            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(itemDto.getQuantity()));
            grandTotal = grandTotal.add(lineTotal);

            PurchaseRequestItem item = PurchaseRequestItem.builder()
                    .purchaseRequest(pr)
                    .product(product)
                    .quantity(itemDto.getQuantity())
                    .estimatedUnitPrice(unitPrice)
                    .totalPrice(lineTotal)
                    .build();

            pr.getItems().add(item);
        }

        pr.setTotalAmount(grandTotal);
        return mapToDto(purchaseRequestRepository.save(pr));
    }

    @Transactional
    public PurchaseRequestDto approvePurchaseRequest(Long id) {
        PurchaseRequest pr = purchaseRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PurchaseRequest", "id", id));

        if (!"SUBMITTED".equals(pr.getStatus()) && !"DRAFT".equals(pr.getStatus())) {
            throw new BusinessRuleException("Only DRAFT or SUBMITTED purchase requests can be approved");
        }

        pr.setStatus("APPROVED");
        return mapToDto(purchaseRequestRepository.save(pr));
    }

    private PurchaseRequestDto mapToDto(PurchaseRequest pr) {
        return PurchaseRequestDto.builder()
                .id(pr.getId())
                .prNumber(pr.getPrNumber())
                .department(pr.getDepartment())
                .status(pr.getStatus())
                .totalAmount(pr.getTotalAmount())
                .requestedById(pr.getRequestedBy().getId())
                .requestedByName(pr.getRequestedBy().getFirstName() + " " + pr.getRequestedBy().getLastName())
                .build();
    }
}
