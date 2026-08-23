package com.smartprocure.modules.procurement.service;

import com.smartprocure.core.payload.PagedResponse;
import com.smartprocure.modules.procurement.dto.PurchaseOrderDTO;

import java.time.LocalDate;

/**
 * Enterprise Service Contract for Purchase Order Contract Lifecycle Management.
 *
 * @author Principal Java Architect
 */
public interface PurchaseOrderService {

    PurchaseOrderDTO createPurchaseOrder(String userEmail, PurchaseOrderDTO orderDTO);

    PurchaseOrderDTO convertRequestToOrder(Long purchaseRequestId, String userEmail, Long vendorId, LocalDate expectedDeliveryDate);

    PurchaseOrderDTO issueOrderToVendor(Long id);

    PurchaseOrderDTO updateOrderStatus(Long id, String status);

    PurchaseOrderDTO getPurchaseOrderById(Long id);

    PagedResponse<PurchaseOrderDTO> getAllPurchaseOrders(int page, int size, String sortBy, String sortDir, String status);

    void cancelPurchaseOrder(Long id);
}
