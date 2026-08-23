package com.smartprocure.modules.procurement.controller;

import com.smartprocure.core.payload.ApiResponse;
import com.smartprocure.core.payload.PagedResponse;
import com.smartprocure.modules.procurement.dto.PurchaseOrderDTO;
import com.smartprocure.modules.procurement.service.PurchaseOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Enterprise REST Controller for Purchase Order Contracts & Vendor Fulfillment.
 *
 * @author Principal Java Architect
 */
@RestController
@RequestMapping("/api/v1/purchase-orders")
@RequiredArgsConstructor
@Tag(name = "Purchase Orders", description = "Endpoints for Vendor Contracts, Request Conversions, and Fulfillment Tracking")
public class PurchaseOrderController {

    private final PurchaseOrderService orderService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER')")
    @Operation(summary = "Create Purchase Order", description = "Generates a new standalone Purchase Order contract.")
    public ResponseEntity<ApiResponse<PurchaseOrderDTO>> createPurchaseOrder(
            Authentication authentication,
            @Valid @RequestBody PurchaseOrderDTO orderDTO) {
        PurchaseOrderDTO created = orderService.createPurchaseOrder(authentication.getName(), orderDTO);
        return new ResponseEntity<>(ApiResponse.success(created, "Purchase Order created successfully"), HttpStatus.CREATED);
    }

    @PostMapping("/convert-request/{purchaseRequestId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER')")
    @Operation(summary = "Convert Purchase Request to Purchase Order", description = "Converts an approved Purchase Request into a formal PO.")
    public ResponseEntity<ApiResponse<PurchaseOrderDTO>> convertRequestToOrder(
            @PathVariable Long purchaseRequestId,
            Authentication authentication,
            @RequestParam Long vendorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expectedDeliveryDate) {
        PurchaseOrderDTO order = orderService.convertRequestToOrder(purchaseRequestId, authentication.getName(), vendorId, expectedDeliveryDate);
        return new ResponseEntity<>(ApiResponse.success(order, "Purchase Request successfully converted to Purchase Order"), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/issue")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER')")
    @Operation(summary = "Issue Order to Vendor", description = "Dispatches the PO contract to the assigned vendor.")
    public ResponseEntity<ApiResponse<PurchaseOrderDTO>> issueOrderToVendor(@PathVariable Long id) {
        PurchaseOrderDTO updated = orderService.issueOrderToVendor(id);
        return ResponseEntity.ok(ApiResponse.success(updated, "Purchase Order issued to vendor successfully"));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER', 'VENDOR', 'WAREHOUSE_MANAGER')")
    @Operation(summary = "Update Purchase Order Status", description = "Updates PO fulfillment status (ACKNOWLEDGED, SHIPPED, DELIVERED, COMPLETED).")
    public ResponseEntity<ApiResponse<PurchaseOrderDTO>> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        PurchaseOrderDTO updated = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(updated, "Purchase Order status updated successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get Purchase Order by ID", description = "Retrieves details of a specific purchase order contract.")
    public ResponseEntity<ApiResponse<PurchaseOrderDTO>> getPurchaseOrderById(@PathVariable Long id) {
        PurchaseOrderDTO order = orderService.getPurchaseOrderById(id);
        return ResponseEntity.ok(ApiResponse.success(order, "Purchase Order details retrieved successfully"));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get Paginated Purchase Orders", description = "Fetches purchase orders list filtered optionally by status.")
    public ResponseEntity<ApiResponse<PagedResponse<PurchaseOrderDTO>>> getAllPurchaseOrders(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir,
            @RequestParam(value = "status", required = false) String status) {

        PagedResponse<PurchaseOrderDTO> response = orderService.getAllPurchaseOrders(page, size, sortBy, sortDir, status);
        return ResponseEntity.ok(ApiResponse.success(response, "Purchase Orders retrieved successfully"));
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER')")
    @Operation(summary = "Cancel Purchase Order", description = "Cancels an active purchase order contract.")
    public ResponseEntity<ApiResponse<Void>> cancelPurchaseOrder(@PathVariable Long id) {
        orderService.cancelPurchaseOrder(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Purchase Order cancelled successfully"));
    }
}
