package com.smartprocure.modules.inventory.service.impl;

import com.smartprocure.core.exception.BusinessRuleViolationException;
import com.smartprocure.core.exception.ResourceNotFoundException;
import com.smartprocure.modules.inventory.dto.InventoryDTO;
import com.smartprocure.modules.inventory.dto.StockTransferRequest;
import com.smartprocure.modules.inventory.entity.Inventory;
import com.smartprocure.modules.inventory.entity.StockMovement;
import com.smartprocure.modules.inventory.entity.Warehouse;
import com.smartprocure.modules.inventory.enums.StockMovementType;
import com.smartprocure.modules.inventory.repository.InventoryRepository;
import com.smartprocure.modules.inventory.repository.StockMovementRepository;
import com.smartprocure.modules.inventory.repository.WarehouseRepository;
import com.smartprocure.modules.inventory.service.InventoryService;
import com.smartprocure.modules.product.entity.Product;
import com.smartprocure.modules.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service Implementation managing inventory ledger reconciliations and stock movements.
 *
 * @author Principal Java Architect
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final StockMovementRepository stockMovementRepository;

    @Override
    @Transactional
    public InventoryDTO updateStock(Long productId, Long warehouseId, Integer quantityChange, String movementTypeStr, String referenceDoc, String remarks) {
        log.info("Updating stock level for Product ID: {} at Warehouse ID: {} with change: {}", productId, warehouseId, quantityChange);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", warehouseId));

        Inventory inventory = inventoryRepository.findByProductIdAndWarehouseId(productId, warehouseId)
                .orElseGet(() -> Inventory.builder()
                        .product(product)
                        .warehouse(warehouse)
                        .quantityOnHand(0)
                        .allocatedQuantity(0)
                        .availableQuantity(0)
                        .build());

        int newQuantityOnHand = inventory.getQuantityOnHand() + quantityChange;
        if (newQuantityOnHand < 0) {
            throw new BusinessRuleViolationException("Insufficient inventory on hand. Current stock: " + inventory.getQuantityOnHand() + ", requested reduction: " + Math.abs(quantityChange));
        }

        inventory.setQuantityOnHand(newQuantityOnHand);
        inventory.recalculateAvailableQuantity();
        Inventory savedInventory = inventoryRepository.save(inventory);

        StockMovementType movementType = StockMovementType.valueOf(movementTypeStr);
        String movementNumber = "STK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        StockMovement movement = StockMovement.builder()
                .movementNumber(movementNumber)
                .product(product)
                .sourceWarehouse(quantityChange < 0 ? warehouse : null)
                .destinationWarehouse(quantityChange > 0 ? warehouse : null)
                .quantity(Math.abs(quantityChange))
                .movementType(movementType)
                .referenceDocument(referenceDoc)
                .remarks(remarks)
                .build();

        stockMovementRepository.save(movement);
        log.info("Stock ledger updated successfully. Transaction ID: {}", movementNumber);

        return mapToDTO(savedInventory);
    }

    @Override
    @Transactional
    public InventoryDTO transferStock(StockTransferRequest transferRequest) {
        log.info("Processing inter-warehouse stock transfer of quantity {} from WH {} to WH {}",
                transferRequest.getQuantity(), transferRequest.getSourceWarehouseId(), transferRequest.getDestinationWarehouseId());

        if (transferRequest.getSourceWarehouseId().equals(transferRequest.getDestinationWarehouseId())) {
            throw new BusinessRuleViolationException("Source and destination warehouse cannot be identical.");
        }

        Product product = productRepository.findById(transferRequest.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", transferRequest.getProductId()));

        Warehouse sourceWarehouse = warehouseRepository.findById(transferRequest.getSourceWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", transferRequest.getSourceWarehouseId()));

        Warehouse destWarehouse = warehouseRepository.findById(transferRequest.getDestinationWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", transferRequest.getDestinationWarehouseId()));

        Inventory sourceInventory = inventoryRepository.findByProductIdAndWarehouseId(product.getId(), sourceWarehouse.getId())
                .orElseThrow(() -> new BusinessRuleViolationException("No inventory ledger exists at source warehouse."));

        if (sourceInventory.getAvailableQuantity() < transferRequest.getQuantity()) {
            throw new BusinessRuleViolationException("Insufficient available stock at source warehouse. Available: " + sourceInventory.getAvailableQuantity());
        }

        sourceInventory.setQuantityOnHand(sourceInventory.getQuantityOnHand() - transferRequest.getQuantity());
        sourceInventory.recalculateAvailableQuantity();
        inventoryRepository.save(sourceInventory);

        Inventory destInventory = inventoryRepository.findByProductIdAndWarehouseId(product.getId(), destWarehouse.getId())
                .orElseGet(() -> Inventory.builder()
                        .product(product)
                        .warehouse(destWarehouse)
                        .quantityOnHand(0)
                        .allocatedQuantity(0)
                        .availableQuantity(0)
                        .build());

        destInventory.setQuantityOnHand(destInventory.getQuantityOnHand() + transferRequest.getQuantity());
        destInventory.recalculateAvailableQuantity();
        Inventory savedDestInventory = inventoryRepository.save(destInventory);

        String movementNumber = "TRF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        StockMovement transferMovement = StockMovement.builder()
                .movementNumber(movementNumber)
                .product(product)
                .sourceWarehouse(sourceWarehouse)
                .destinationWarehouse(destWarehouse)
                .quantity(transferRequest.getQuantity())
                .movementType(StockMovementType.INTERNAL_TRANSFER)
                .referenceDocument("STOCK-TRANSFER")
                .remarks(transferRequest.getRemarks())
                .build();

        stockMovementRepository.save(transferMovement);
        log.info("Inter-warehouse stock transfer completed successfully under transaction {}", movementNumber);

        return mapToDTO(savedDestInventory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryDTO> getInventoryByWarehouse(Long warehouseId) {
        return inventoryRepository.findByWarehouseId(warehouseId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryDTO> getInventoryByProduct(Long productId) {
        return inventoryRepository.findByProductId(productId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryDTO getStockLevel(Long productId, Long warehouseId) {
        Inventory inventory = inventoryRepository.findByProductIdAndWarehouseId(productId, warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory record not found for product ID " + productId + " at warehouse ID " + warehouseId));
        return mapToDTO(inventory);
    }

    private InventoryDTO mapToDTO(Inventory inventory) {
        return InventoryDTO.builder()
                .id(inventory.getId())
                .productId(inventory.getProduct().getId())
                .productSku(inventory.getProduct().getSku())
                .productName(inventory.getProduct().getName())
                .warehouseId(inventory.getWarehouse().getId())
                .warehouseCode(inventory.getWarehouse().getCode())
                .warehouseName(inventory.getWarehouse().getName())
                .quantityOnHand(inventory.getQuantityOnHand())
                .allocatedQuantity(inventory.getAllocatedQuantity())
                .availableQuantity(inventory.getAvailableQuantity())
                .build();
    }
}
