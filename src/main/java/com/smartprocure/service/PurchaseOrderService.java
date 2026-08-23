package com.smartprocure.service;

import com.smartprocure.domain.entity.PurchaseRequest;
import com.smartprocure.domain.enums.PurchaseOrderStatus;
import com.smartprocure.domain.enums.PurchaseRequestStatus;
import com.smartprocure.dto.request.PurchaseOrderCreateDTO;
import com.smartprocure.dto.request.PurchaseRequestCreateDTO;
import com.smartprocure.dto.response.PurchaseOrderResponseDTO;

import java.util.List;

public interface PurchaseOrderService {
    PurchaseOrderResponseDTO createPO(PurchaseOrderCreateDTO createDTO, Long createdById);
    PurchaseOrderResponseDTO updatePOStatus(Long id, PurchaseOrderStatus status);
    PurchaseOrderResponseDTO updatePOStatusByIdentifier(String identifier, PurchaseOrderStatus status);
    List<PurchaseOrderResponseDTO> getAllPOs();
    List<PurchaseOrderResponseDTO> getAllPurchaseOrders();
    PurchaseOrderResponseDTO getPOById(Long id);
    PurchaseOrderResponseDTO getPOByIdOrNumber(String identifier);

    List<PurchaseRequest> getAllPurchaseRequests();
    PurchaseRequest createPR(PurchaseRequestCreateDTO createDTO, Long requestedById);
    PurchaseRequest updatePRStatus(Long id, PurchaseRequestStatus status);
    PurchaseRequest updatePRStatus(Long id, PurchaseRequestStatus status, String rejectionReason);
    PurchaseOrderResponseDTO createPOFromPR(Long prId);
    PurchaseOrderResponseDTO createPOFromPR(Long prId, Long vendorId, java.time.LocalDate deliveryDate, String paymentTerms);
}
