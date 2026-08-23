package com.smartprocure.web.rest;

import com.smartprocure.domain.entity.Inventory;
import com.smartprocure.dto.response.ApiResponse;
import com.smartprocure.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.smartprocure.dto.response.InventoryResponseDTO;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory & Stock Management", description = "Endpoints for multi-warehouse inventory tracking and stock adjustments")
public class InventoryRestController {

    private final InventoryService inventoryService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'PROCUREMENT_MANAGER')")
    @Operation(summary = "Get All Inventory Items", description = "Retrieve complete list of stock items across all warehouse hubs")
    public ResponseEntity<ApiResponse> getAllInventory() {
        List<InventoryResponseDTO> items = inventoryService.getAllInventoryDTOs();
        return ResponseEntity.ok(ApiResponse.ok("Inventory items retrieved successfully", items));
    }

    @PostMapping("/adjust")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'PROCUREMENT_MANAGER')")
    @Operation(summary = "Adjust Stock Level", description = "Adjust stock quantity (increase/decrease) for a product at a specific warehouse")
    public ResponseEntity<ApiResponse> adjustStock(@RequestBody StockAdjustmentDTO adjustmentDTO) {
        if (adjustmentDTO.getInventoryId() != null) {
            InventoryResponseDTO updatedDTO = inventoryService.adjustStockLevel(
                    adjustmentDTO.getInventoryId(),
                    adjustmentDTO.getQuantity(),
                    adjustmentDTO.getAdjustmentType(),
                    adjustmentDTO.getReason()
            );
            return ResponseEntity.ok(ApiResponse.ok("Stock level adjusted successfully", updatedDTO));
        }

        int qty = adjustmentDTO.getQuantity() != null ? adjustmentDTO.getQuantity() : 0;
        if ("DECREASE".equalsIgnoreCase(adjustmentDTO.getAdjustmentType())) {
            qty = -qty;
        }
        com.smartprocure.domain.entity.Inventory updatedInv = inventoryService.updateStock(
                adjustmentDTO.getProductId(),
                adjustmentDTO.getWarehouseId(),
                qty
        );
        return ResponseEntity.ok(ApiResponse.ok("Stock level adjusted successfully", updatedInv));
    }

    @Getter
    @Setter
    public static class StockAdjustmentDTO {
        private Long inventoryId;
        private Long productId;
        private Long warehouseId;
        private Integer quantity;
        private String adjustmentType; // INCREASE or DECREASE
        private String reason;
    }
}
