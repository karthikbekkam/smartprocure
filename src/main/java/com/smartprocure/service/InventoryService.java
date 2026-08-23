package com.smartprocure.service;

import com.smartprocure.domain.entity.Inventory;
import com.smartprocure.dto.request.StockTransferRequestDTO;

import com.smartprocure.dto.response.InventoryResponseDTO;

import java.util.List;

public interface InventoryService {
    Inventory updateStock(Long productId, Long warehouseId, Integer quantity);
    List<Inventory> getInventoryByWarehouse(Long warehouseId);
    List<Inventory> getLowStockItems();
    List<Inventory> getAllInventoryItems();
    List<InventoryResponseDTO> getAllInventoryDTOs();
    InventoryResponseDTO adjustStockLevel(Long inventoryId, Integer quantity, String adjustmentType, String reason);
    void processStockTransfer(StockTransferRequestDTO transferDTO, Long requestedById);
}
