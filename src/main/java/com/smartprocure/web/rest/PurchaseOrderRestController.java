package com.smartprocure.web.rest;

import com.smartprocure.domain.entity.PurchaseRequest;
import com.smartprocure.domain.enums.PurchaseOrderStatus;
import com.smartprocure.domain.enums.PurchaseRequestStatus;
import com.smartprocure.dto.request.PurchaseOrderCreateDTO;
import com.smartprocure.dto.response.ApiResponse;
import com.smartprocure.dto.response.PurchaseOrderResponseDTO;
import com.smartprocure.service.PurchaseOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/purchase-orders")
@RequiredArgsConstructor
@Tag(name = "Purchase Orders & Requisitions", description = "Endpoints for PR approval routing and PO creation")
public class PurchaseOrderRestController {

    private final PurchaseOrderService purchaseOrderService;

    @GetMapping
    @Operation(summary = "Get All Purchase Orders")
    public ResponseEntity<ApiResponse> getAllPurchaseOrders() {
        List<PurchaseOrderResponseDTO> pos = purchaseOrderService.getAllPurchaseOrders();
        return ResponseEntity.ok(ApiResponse.ok("Purchase orders retrieved", pos));
    }

    @GetMapping("/requests")
    @Operation(summary = "Get All Purchase Requisitions")
    public ResponseEntity<ApiResponse> getAllPurchaseRequests() {
        List<PurchaseRequest> prs = purchaseOrderService.getAllPurchaseRequests();
        return ResponseEntity.ok(ApiResponse.ok("Purchase requisitions retrieved", prs));
    }

    @GetMapping("/{identifier}")
    @Operation(summary = "Get Purchase Order by ID or PO Number")
    public ResponseEntity<ApiResponse> getPOById(@PathVariable String identifier) {
        PurchaseOrderResponseDTO po = purchaseOrderService.getPOByIdOrNumber(identifier);
        return ResponseEntity.ok(ApiResponse.ok("Purchase Order details retrieved", po));
    }

    @PostMapping
    @Operation(summary = "Create New Purchase Order")
    public ResponseEntity<ApiResponse> createPO(@Valid @RequestBody PurchaseOrderCreateDTO createDTO) {
        Long createdById = 1L;
        PurchaseOrderResponseDTO po = purchaseOrderService.createPO(createDTO, createdById);
        return ResponseEntity.ok(ApiResponse.ok("Purchase Order " + po.getPoNumber() + " created successfully", po));
    }

    @PatchMapping("/{identifier}/status")
    @Operation(summary = "Update Purchase Order Status")
    public ResponseEntity<ApiResponse> updatePOStatus(
            @PathVariable String identifier,
            @RequestParam PurchaseOrderStatus status
    ) {
        PurchaseOrderResponseDTO po = purchaseOrderService.updatePOStatusByIdentifier(identifier, status);
        return ResponseEntity.ok(ApiResponse.ok("Purchase Order " + po.getPoNumber() + " status updated to " + status + " successfully", po));
    }

    @PostMapping("/requests")
    @Operation(summary = "Submit New Purchase Request")
    public ResponseEntity<ApiResponse> createPR(@Valid @RequestBody com.smartprocure.dto.request.PurchaseRequestCreateDTO createDTO) {
        Long requestedById = 1L;
        PurchaseRequest pr = purchaseOrderService.createPR(createDTO, requestedById);
        return ResponseEntity.ok(ApiResponse.ok("Purchase Request " + pr.getPrNumber() + " submitted successfully", pr));
    }

    @PatchMapping("/requests/{id}/status")
    @Operation(summary = "Approve or Reject Purchase Request")
    public ResponseEntity<ApiResponse> updatePRStatus(
            @PathVariable Long id,
            @RequestParam PurchaseRequestStatus status,
            @RequestParam(required = false) String rejectionReason
    ) {
        PurchaseRequest updatedPr = purchaseOrderService.updatePRStatus(id, status, rejectionReason);
        return ResponseEntity.ok(ApiResponse.ok("Purchase Request status updated to " + status, updatedPr));
    }

    @PostMapping("/requests/{id}/create-po")
    @Operation(summary = "Create Purchase Order from Approved PR")
    public ResponseEntity<ApiResponse> createPOFromPR(
            @PathVariable Long id,
            @RequestParam(required = false) Long vendorId,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate deliveryDate,
            @RequestParam(required = false) String paymentTerms
    ) {
        PurchaseOrderResponseDTO po = purchaseOrderService.createPOFromPR(id, vendorId, deliveryDate, paymentTerms);
        return ResponseEntity.ok(ApiResponse.ok("Purchase Order " + po.getPoNumber() + " created successfully", po));
    }
}
