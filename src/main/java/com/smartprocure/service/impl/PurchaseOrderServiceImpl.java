package com.smartprocure.service.impl;

import com.smartprocure.domain.entity.PurchaseOrder;
import com.smartprocure.domain.entity.PurchaseRequest;
import com.smartprocure.domain.entity.User;
import com.smartprocure.domain.entity.Vendor;
import com.smartprocure.domain.enums.PurchaseOrderStatus;
import com.smartprocure.domain.enums.PurchaseRequestStatus;
import com.smartprocure.domain.repository.PurchaseOrderRepository;
import com.smartprocure.domain.repository.PurchaseRequestRepository;
import com.smartprocure.domain.repository.UserRepository;
import com.smartprocure.domain.repository.VendorRepository;
import com.smartprocure.dto.request.PurchaseOrderCreateDTO;
import com.smartprocure.dto.response.PurchaseOrderResponseDTO;
import com.smartprocure.exception.BadRequestException;
import com.smartprocure.exception.ResourceNotFoundException;
import com.smartprocure.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseRequestRepository purchaseRequestRepository;
    private final VendorRepository vendorRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public PurchaseOrderResponseDTO createPO(PurchaseOrderCreateDTO createDTO, Long createdById) {
        Vendor vendor = vendorRepository.findById(createDTO.getVendorId())
                .orElseThrow(() -> new ResourceNotFoundException("Vendor", "id", createDTO.getVendorId()));

        User createdBy = userRepository.findById(createdById)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", createdById));

        PurchaseOrder po = PurchaseOrder.builder()
                .poNumber("PO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .vendor(vendor)
                .createdByUser(createdBy)
                .status(PurchaseOrderStatus.ISSUED)
                .totalAmount(BigDecimal.valueOf(2500.00))
                .deliveryDate(createDTO.getDeliveryDate())
                .build();

        PurchaseOrder savedPO = purchaseOrderRepository.save(po);
        return mapToDTO(savedPO);
    }

    @Override
    @Transactional
    public PurchaseOrderResponseDTO updatePOStatus(Long id, PurchaseOrderStatus status) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PurchaseOrder", "id", id));
        po.setStatus(status);
        return mapToDTO(purchaseOrderRepository.save(po));
    }

    @Override
    @Transactional
    public PurchaseOrderResponseDTO updatePOStatusByIdentifier(String identifier, PurchaseOrderStatus status) {
        PurchaseOrder po;
        try {
            Long id = Long.parseLong(identifier);
            po = purchaseOrderRepository.findById(id)
                    .orElseGet(() -> purchaseOrderRepository.findByPoNumber(identifier)
                            .orElseThrow(() -> new ResourceNotFoundException("PurchaseOrder", "identifier", identifier)));
        } catch (NumberFormatException e) {
            po = purchaseOrderRepository.findByPoNumber(identifier)
                    .orElseThrow(() -> new ResourceNotFoundException("PurchaseOrder", "poNumber", identifier));
        }
        po.setStatus(status);
        PurchaseOrder saved = purchaseOrderRepository.save(po);
        return mapToDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseOrderResponseDTO> getAllPOs() {
        return purchaseOrderRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseOrderResponseDTO> getAllPurchaseOrders() {
        return getAllPOs();
    }

    @Override
    @Transactional(readOnly = true)
    public PurchaseOrderResponseDTO getPOById(Long id) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PurchaseOrder", "id", id));
        return mapToDTO(po);
    }

    @Override
    @Transactional(readOnly = true)
    public PurchaseOrderResponseDTO getPOByIdOrNumber(String identifier) {
        PurchaseOrder po;
        try {
            Long id = Long.parseLong(identifier);
            po = purchaseOrderRepository.findById(id)
                    .orElseGet(() -> purchaseOrderRepository.findByPoNumber(identifier)
                            .orElseThrow(() -> new ResourceNotFoundException("PurchaseOrder", "identifier", identifier)));
        } catch (NumberFormatException e) {
            po = purchaseOrderRepository.findByPoNumber(identifier)
                    .orElseThrow(() -> new ResourceNotFoundException("PurchaseOrder", "poNumber", identifier));
        }
        return mapToDTO(po);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseRequest> getAllPurchaseRequests() {
        return purchaseRequestRepository.findAll();
    }

    @Override
    @Transactional
    public PurchaseRequest createPR(com.smartprocure.dto.request.PurchaseRequestCreateDTO createDTO, Long requestedById) {
        User requestedBy = null;
        if (requestedById != null) {
            requestedBy = userRepository.findById(requestedById).orElse(null);
        }
        if (requestedBy == null) {
            requestedBy = userRepository.findAll().stream().findFirst().orElse(null);
        }

        PurchaseRequest pr = PurchaseRequest.builder()
                .prNumber("PR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .department(createDTO.getDepartment())
                .totalAmount(createDTO.getTotalAmount() != null ? createDTO.getTotalAmount() : BigDecimal.ZERO)
                .status(PurchaseRequestStatus.SUBMITTED)
                .requestedBy(requestedBy)
                .build();

        return purchaseRequestRepository.save(pr);
    }

    @Override
    @Transactional
    public PurchaseRequest updatePRStatus(Long id, PurchaseRequestStatus status) {
        return updatePRStatus(id, status, null);
    }

    @Override
    @Transactional
    public PurchaseRequest updatePRStatus(Long id, PurchaseRequestStatus status, String rejectionReason) {
        PurchaseRequest pr = purchaseRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PurchaseRequest", "id", id));
        pr.setStatus(status);
        if (status == PurchaseRequestStatus.REJECTED && rejectionReason != null && !rejectionReason.isBlank()) {
            pr.setRejectionReason(rejectionReason);
        }
        return purchaseRequestRepository.save(pr);
    }

    @Override
    @Transactional
    public PurchaseOrderResponseDTO createPOFromPR(Long prId) {
        return createPOFromPR(prId, null, null, null);
    }

    @Override
    @Transactional
    public PurchaseOrderResponseDTO createPOFromPR(Long prId, Long vendorId, java.time.LocalDate deliveryDate, String paymentTerms) {
        PurchaseRequest pr = purchaseRequestRepository.findById(prId)
                .orElseThrow(() -> new ResourceNotFoundException("PurchaseRequest", "id", prId));

        if (pr.getStatus() != PurchaseRequestStatus.APPROVED) {
            throw new BadRequestException("Only APPROVED Purchase Requests can be converted to Purchase Orders.");
        }

        if (purchaseOrderRepository.findByPurchaseRequestId(prId).isPresent()) {
            throw new BadRequestException("A Purchase Order has already been generated for Purchase Requisition " + pr.getPrNumber() + ".");
        }

        Vendor vendor = null;
        if (vendorId != null) {
            vendor = vendorRepository.findById(vendorId).orElse(null);
        }
        if (vendor == null) {
            vendor = vendorRepository.findAll().stream().findFirst()
                    .orElseThrow(() -> new BadRequestException("No active vendor found in system for PO assignment."));
        }

        User defaultUser = userRepository.findAll().stream().findFirst().orElse(null);

        String poNumber = "PO-2026-" + String.format("%03d", purchaseOrderRepository.count() + 5);
        if (purchaseOrderRepository.findByPoNumber(poNumber).isPresent()) {
            poNumber = "PO-2026-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        }

        PurchaseOrder po = PurchaseOrder.builder()
                .poNumber(poNumber)
                .purchaseRequest(pr)
                .vendor(vendor)
                .createdByUser(defaultUser)
                .status(PurchaseOrderStatus.ISSUED)
                .totalAmount(pr.getTotalAmount() != null ? pr.getTotalAmount() : BigDecimal.valueOf(4500.00))
                .deliveryDate(deliveryDate != null ? deliveryDate : java.time.LocalDate.now().plusDays(14))
                .build();

        PurchaseOrder savedPO = purchaseOrderRepository.save(po);
        return mapToDTO(savedPO);
    }

    private PurchaseOrderResponseDTO mapToDTO(PurchaseOrder po) {
        return PurchaseOrderResponseDTO.builder()
                .id(po.getId())
                .poNumber(po.getPoNumber())
                .vendorName(po.getVendor() != null ? po.getVendor().getCompanyName() : "Unassigned Vendor")
                .status(po.getStatus())
                .totalAmount(po.getTotalAmount())
                .deliveryDate(po.getDeliveryDate())
                .createdAt(po.getCreatedAt())
                .build();
    }
}
