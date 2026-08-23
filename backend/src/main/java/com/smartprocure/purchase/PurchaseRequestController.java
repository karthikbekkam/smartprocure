package com.smartprocure.purchase;

import com.smartprocure.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/purchase-requests")
@RequiredArgsConstructor
public class PurchaseRequestController {

    private final PurchaseRequestService purchaseRequestService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PurchaseRequestDto>>> getAllPurchaseRequests(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok("Purchase requests fetched successfully", purchaseRequestService.getAllPurchaseRequests(pageable)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PurchaseRequestDto>> createPurchaseRequest(@Valid @RequestBody PurchaseRequestDto dto) {
        return ResponseEntity.ok(ApiResponse.ok("Purchase request created", purchaseRequestService.createPurchaseRequest(dto)));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<PurchaseRequestDto>> approvePurchaseRequest(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Purchase request approved", purchaseRequestService.approvePurchaseRequest(id)));
    }
}
