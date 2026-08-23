package com.smartprocure.modules.inventory.controller;

import com.smartprocure.core.payload.ApiResponse;
import com.smartprocure.modules.inventory.dto.InventoryDTO;
import com.smartprocure.modules.inventory.dto.StockTransferRequest;
import com.smartprocure.modules.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Enterprise REST Controller for Inventory Stock Levels & Stock Transfers.
 *
 * @author Principal Java Architect
 */
@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory & Stock Control", description = "Endpoints for Stock Level Adjustments, Stock Transfers, and Warehouse Inventories")
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping("/adjust")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'PROCUREMENT_MANAGER')")
    @Operation(summary = "Adjust Stock Quantity", description = "Increases or decreases inventory quantity on hand at a specified warehouse.")
    public ResponseEntity<ApiResponse<InventoryDTO>> updateStock(
            @RequestParam Long productId,
            @RequestParam Long warehouseId,
            @RequestParam Integer quantityChange,
            @RequestParam String movementType,
            @RequestParam(required = false) String referenceDoc,
            @RequestParam(required = false) String remarks) {

        InventoryDTO updated = inventoryService.updateStock(productId, warehouseId, quantityChange, movementType, referenceDoc, remarks);
        return ResponseEntity.ok(ApiResponse.success(updated, "Inventory stock level updated successfully"));
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    @Operation(summary = "Transfer Stock Between Warehouses", description = "Executes an internal stock transfer from source to destination warehouse.")
    public ResponseEntity<ApiResponse<InventoryDTO>> transferStock(@Valid @RequestBody StockTransferRequest transferRequest) {
        InventoryDTO transferred = inventoryService.transferStock(transferRequest);
        return ResponseEntity.ok(ApiResponse.success(transferred, "Stock transfer completed successfully"));
    }

    @GetMapping("/warehouse/{warehouseId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get Inventory by Warehouse", description = "Fetches all product stock records stored at a specific warehouse.")
    public ResponseEntity<ApiResponse<List<InventoryDTO>>> getInventoryByWarehouse(@PathVariable Long warehouseId) {
        List<InventoryDTO> inventory = inventoryService.getInventoryByWarehouse(warehouseId);
        return ResponseEntity.ok(ApiResponse.success(inventory, "Warehouse stock ledger retrieved successfully"));
    }

    @GetMapping("/product/{productId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get Inventory by Product", description = "Fetches stock levels across all warehouses for a specific product.")
    public ResponseEntity<ApiResponse<List<InventoryDTO>>> getInventoryByProduct(@PathVariable Long productId) {
        List<InventoryDTO> inventory = inventoryService.getInventoryByProduct(productId);
        return ResponseEntity.ok(ApiResponse.success(inventory, "Product stock distribution retrieved successfully"));
    }

    @GetMapping("/level")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get Specific Product Warehouse Stock Level", description = "Checks stock level for a product at a specific warehouse.")
    public ResponseEntity<ApiResponse<InventoryDTO>> getStockLevel(
            @RequestParam Long productId,
            @RequestParam Long warehouseId) {
        InventoryDTO inventory = inventoryService.getStockLevel(productId, warehouseId);
        return ResponseEntity.ok(ApiResponse.success(inventory, "Stock level retrieved successfully"));
    }
}
