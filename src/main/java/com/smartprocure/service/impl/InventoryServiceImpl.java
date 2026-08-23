package com.smartprocure.service.impl;

import com.smartprocure.domain.entity.Inventory;
import com.smartprocure.domain.entity.Product;
import com.smartprocure.domain.entity.StockTransfer;
import com.smartprocure.domain.entity.User;
import com.smartprocure.domain.entity.Warehouse;
import com.smartprocure.domain.enums.TransferStatus;
import com.smartprocure.domain.repository.InventoryRepository;
import com.smartprocure.domain.repository.ProductRepository;
import com.smartprocure.domain.repository.StockTransferRepository;
import com.smartprocure.domain.repository.UserRepository;
import com.smartprocure.domain.repository.WarehouseRepository;
import com.smartprocure.dto.request.StockTransferRequestDTO;
import com.smartprocure.exception.InsufficientStockException;
import com.smartprocure.exception.ResourceNotFoundException;
import com.smartprocure.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final StockTransferRepository stockTransferRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Inventory updateStock(Long productId, Long warehouseId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", warehouseId));

        Inventory inventory = inventoryRepository.findByProductIdAndWarehouseId(productId, warehouseId)
                .orElseGet(() -> Inventory.builder()
                        .product(product)
                        .warehouse(warehouse)
                        .quantityOnHand(0)
                        .quantityAllocated(0)
                        .quantityAvailable(0)
                        .minStockLevel(5)
                        .build());

        inventory.setQuantityOnHand(inventory.getQuantityOnHand() + quantity);
        inventory.setQuantityAvailable(inventory.getQuantityOnHand() - inventory.getQuantityAllocated());

        return inventoryRepository.save(inventory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inventory> getInventoryByWarehouse(Long warehouseId) {
        return inventoryRepository.findByWarehouseId(warehouseId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inventory> getLowStockItems() {
        return inventoryRepository.findLowStockItems();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inventory> getAllInventoryItems() {
        return inventoryRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<com.smartprocure.dto.response.InventoryResponseDTO> getAllInventoryDTOs() {
        return inventoryRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    @Transactional
    public com.smartprocure.dto.response.InventoryResponseDTO adjustStockLevel(Long inventoryId, Integer quantity, String adjustmentType, String reason) {
        if (quantity == null || quantity <= 0) {
            throw new com.smartprocure.exception.BadRequestException("Adjustment quantity must be greater than zero.");
        }

        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory record not found.", "id", inventoryId));

        int currentQty = inventory.getQuantityOnHand() != null ? inventory.getQuantityOnHand() : 0;
        int allocated = inventory.getQuantityAllocated() != null ? inventory.getQuantityAllocated() : 0;

        int newQty;
        if ("DECREASE".equalsIgnoreCase(adjustmentType)) {
            if (currentQty - quantity < 0) {
                throw new com.smartprocure.exception.BadRequestException("Stock cannot be reduced below zero.");
            }
            newQty = currentQty - quantity;
        } else {
            newQty = currentQty + quantity;
        }

        inventory.setQuantityOnHand(newQty);
        inventory.setQuantityAvailable(Math.max(0, newQty - allocated));

        Inventory saved = inventoryRepository.save(inventory);
        return mapToDTO(saved);
    }

    private com.smartprocure.dto.response.InventoryResponseDTO mapToDTO(Inventory inv) {
        int qtyHand = inv.getQuantityOnHand() != null ? inv.getQuantityOnHand() : 0;
        int qtyAllocated = inv.getQuantityAllocated() != null ? inv.getQuantityAllocated() : 0;
        int qtyAvailable = Math.max(0, qtyHand - qtyAllocated);
        int minStock = inv.getMinStockLevel() != null ? inv.getMinStockLevel() : 5;

        String alert;
        if (qtyHand <= 0) {
            alert = "OUT OF STOCK";
        } else if (qtyAvailable <= minStock) {
            alert = "LOW STOCK";
        } else {
            alert = "NORMAL";
        }

        return com.smartprocure.dto.response.InventoryResponseDTO.builder()
                .id(inv.getId())
                .inventoryCode("INV-" + inv.getId())
                .productId(inv.getProduct() != null ? inv.getProduct().getId() : null)
                .productSku(inv.getProduct() != null ? inv.getProduct().getSku() : "N/A")
                .productName(inv.getProduct() != null ? inv.getProduct().getName() : "Unassigned Product")
                .warehouseId(inv.getWarehouse() != null ? inv.getWarehouse().getId() : null)
                .warehouseCode(inv.getWarehouse() != null ? inv.getWarehouse().getCode() : "N/A")
                .warehouseName(inv.getWarehouse() != null ? inv.getWarehouse().getName() : "Unassigned Hub")
                .warehouseLocation(inv.getWarehouse() != null ? inv.getWarehouse().getLocation() : "N/A")
                .quantityOnHand(qtyHand)
                .quantityAllocated(qtyAllocated)
                .quantityAvailable(qtyAvailable)
                .minStockLevel(minStock)
                .stockAlert(alert)
                .updatedAt(inv.getUpdatedAt() != null ? inv.getUpdatedAt() : inv.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public void processStockTransfer(StockTransferRequestDTO transferDTO, Long requestedById) {
        Inventory sourceInv = inventoryRepository.findByProductIdAndWarehouseId(
                transferDTO.getProductId(), transferDTO.getSourceWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "productId", transferDTO.getProductId()));

        if (sourceInv.getQuantityAvailable() < transferDTO.getQuantity()) {
            throw new InsufficientStockException("Insufficient stock available for transfer. Available: " 
                    + sourceInv.getQuantityAvailable() + ", Requested: " + transferDTO.getQuantity());
        }

        // Deduct from Source
        sourceInv.setQuantityOnHand(sourceInv.getQuantityOnHand() - transferDTO.getQuantity());
        sourceInv.setQuantityAvailable(sourceInv.getQuantityOnHand() - sourceInv.getQuantityAllocated());
        inventoryRepository.save(sourceInv);

        // Add to Target
        updateStock(transferDTO.getProductId(), transferDTO.getTargetWarehouseId(), transferDTO.getQuantity());

        // Audit Stock Transfer Record
        User requestedBy = userRepository.findById(requestedById)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", requestedById));

        StockTransfer transfer = StockTransfer.builder()
                .transferNumber("TRF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .sourceWarehouse(sourceInv.getWarehouse())
                .targetWarehouse(warehouseRepository.findById(transferDTO.getTargetWarehouseId()).orElseThrow())
                .product(sourceInv.getProduct())
                .quantity(transferDTO.getQuantity())
                .status(TransferStatus.COMPLETED)
                .requestedBy(requestedBy)
                .notes(transferDTO.getNotes())
                .build();

        stockTransferRepository.save(transfer);
    }
}
