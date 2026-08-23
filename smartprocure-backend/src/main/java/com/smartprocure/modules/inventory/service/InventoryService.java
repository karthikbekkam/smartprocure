package com.smartprocure.modules.inventory.service;

import com.smartprocure.modules.inventory.dto.InventoryDTO;
import com.smartprocure.modules.inventory.dto.StockTransferRequest;

import java.util.List;

/**
 * Enterprise Service Contract for Inventory Ledger Updates & Inter-Warehouse Stock Transfers.
 *
 * @author Principal Java Architect
 */
public interface InventoryService {

    InventoryDTO updateStock(Long productId, Long warehouseId, Integer quantityChange, String movementType, String referenceDoc, String remarks);

    InventoryDTO transferStock(StockTransferRequest transferRequest);

    List<InventoryDTO> getInventoryByWarehouse(Long warehouseId);

    List<InventoryDTO> getInventoryByProduct(Long productId);

    InventoryDTO getStockLevel(Long productId, Long warehouseId);
}
