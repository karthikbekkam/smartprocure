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
@RequestMapping("/api/v1/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PurchaseOrderDto>>> getAllPurchaseOrders(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok("Purchase orders fetched successfully", purchaseOrderService.getAllPurchaseOrders(pageable)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PurchaseOrderDto>> createPurchaseOrder(@Valid @RequestBody PurchaseOrderDto dto) {
        return ResponseEntity.ok(ApiResponse.ok("Purchase order created", purchaseOrderService.createPurchaseOrder(dto)));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<PurchaseOrderDto>> approvePurchaseOrder(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Purchase order approved", purchaseOrderService.approvePurchaseOrder(id)));
    }

    @PostMapping("/{id}/send-to-vendor")
    public ResponseEntity<ApiResponse<PurchaseOrderDto>> sendToVendor(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Purchase order sent to vendor", purchaseOrderService.sendToVendor(id)));
    }
}
