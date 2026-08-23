package com.smartprocure.modules.procurement.controller;

import com.smartprocure.core.payload.ApiResponse;
import com.smartprocure.core.payload.PagedResponse;
import com.smartprocure.modules.procurement.dto.PurchaseRequestDTO;
import com.smartprocure.modules.procurement.service.PurchaseRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Enterprise REST Controller for Purchase Requisitions & Approval Workflows.
 *
 * @author Principal Java Architect
 */
@RestController
@RequestMapping("/api/v1/purchase-requests")
@RequiredArgsConstructor
@Tag(name = "Purchase Requests", description = "Endpoints for Internal Requisition Submissions, Approvals, and Rejections")
public class PurchaseRequestController {

    private final PurchaseRequestService requestService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create Purchase Requisition", description = "Submits a new draft purchase request for internal department demands.")
    public ResponseEntity<ApiResponse<PurchaseRequestDTO>> createPurchaseRequest(
            Authentication authentication,
            @Valid @RequestBody PurchaseRequestDTO requestDTO) {
        PurchaseRequestDTO created = requestService.createPurchaseRequest(authentication.getName(), requestDTO);
        return new ResponseEntity<>(ApiResponse.success(created, "Purchase Request created successfully"), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/submit")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Submit Purchase Request for Approval", description = "Changes PR status from DRAFT to SUBMITTED.")
    public ResponseEntity<ApiResponse<PurchaseRequestDTO>> submitPurchaseRequest(@PathVariable Long id) {
        PurchaseRequestDTO updated = requestService.submitPurchaseRequest(id);
        return ResponseEntity.ok(ApiResponse.success(updated, "Purchase Request submitted for approval"));
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER')")
    @Operation(summary = "Approve Purchase Request", description = "Approves a submitted purchase requisition.")
    public ResponseEntity<ApiResponse<PurchaseRequestDTO>> approvePurchaseRequest(@PathVariable Long id) {
        PurchaseRequestDTO updated = requestService.approvePurchaseRequest(id);
        return ResponseEntity.ok(ApiResponse.success(updated, "Purchase Request approved successfully"));
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER')")
    @Operation(summary = "Reject Purchase Request", description = "Rejects a purchase requisition with a mandatory reason.")
    public ResponseEntity<ApiResponse<PurchaseRequestDTO>> rejectPurchaseRequest(
            @PathVariable Long id,
            @RequestParam String reason) {
        PurchaseRequestDTO updated = requestService.rejectPurchaseRequest(id, reason);
        return ResponseEntity.ok(ApiResponse.success(updated, "Purchase Request rejected"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get Purchase Request by ID", description = "Retrieves details of a specific purchase requisition.")
    public ResponseEntity<ApiResponse<PurchaseRequestDTO>> getPurchaseRequestById(@PathVariable Long id) {
        PurchaseRequestDTO request = requestService.getPurchaseRequestById(id);
        return ResponseEntity.ok(ApiResponse.success(request, "Purchase Request details retrieved successfully"));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get Paginated Purchase Requests", description = "Fetches purchase requisitions list filtered optionally by status.")
    public ResponseEntity<ApiResponse<PagedResponse<PurchaseRequestDTO>>> getAllPurchaseRequests(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir,
            @RequestParam(value = "status", required = false) String status) {

        PagedResponse<PurchaseRequestDTO> response = requestService.getAllPurchaseRequests(page, size, sortBy, sortDir, status);
        return ResponseEntity.ok(ApiResponse.success(response, "Purchase Requests retrieved successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER')")
    @Operation(summary = "Delete Purchase Request", description = "Deletes a draft or rejected purchase request.")
    public ResponseEntity<ApiResponse<Void>> deletePurchaseRequest(@PathVariable Long id) {
        requestService.deletePurchaseRequest(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Purchase Request deleted successfully"));
    }
}
