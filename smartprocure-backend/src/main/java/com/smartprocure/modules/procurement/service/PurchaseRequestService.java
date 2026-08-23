package com.smartprocure.modules.procurement.service;

import com.smartprocure.core.payload.PagedResponse;
import com.smartprocure.modules.procurement.dto.PurchaseRequestDTO;

/**
 * Enterprise Service Contract for Purchase Request (Requisition) Workflows.
 *
 * @author Principal Java Architect
 */
public interface PurchaseRequestService {

    PurchaseRequestDTO createPurchaseRequest(String userEmail, PurchaseRequestDTO requestDTO);

    PurchaseRequestDTO submitPurchaseRequest(Long id);

    PurchaseRequestDTO approvePurchaseRequest(Long id);

    PurchaseRequestDTO rejectPurchaseRequest(Long id, String reason);

    PurchaseRequestDTO getPurchaseRequestById(Long id);

    PagedResponse<PurchaseRequestDTO> getAllPurchaseRequests(int page, int size, String sortBy, String sortDir, String status);

    void deletePurchaseRequest(Long id);
}
