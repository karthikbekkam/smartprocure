package com.smartprocure.inventory;

import com.smartprocure.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<InventoryDto>>> getAllInventory(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok("Inventory items fetched successfully", inventoryService.getAllInventory(pageable)));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponse<List<InventoryDto>>> getLowStockAlerts() {
        return ResponseEntity.ok(ApiResponse.ok("Low stock alerts fetched", inventoryService.getLowStockAlerts()));
    }

    @PostMapping("/adjust")
    public ResponseEntity<ApiResponse<InventoryDto>> adjustStock(@RequestParam Long productId,
                                                                  @RequestParam Long warehouseId,
                                                                  @RequestParam Integer quantityAdjustment) {
        return ResponseEntity.ok(ApiResponse.ok("Stock level adjusted successfully", inventoryService.updateStock(productId, warehouseId, quantityAdjustment)));
    }
}
