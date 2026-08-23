package com.smartprocure.inventory;

import com.smartprocure.common.exception.ResourceNotFoundException;
import com.smartprocure.domain.Inventory;
import com.smartprocure.domain.Product;
import com.smartprocure.domain.Warehouse;
import com.smartprocure.domain.repository.InventoryRepository;
import com.smartprocure.domain.repository.ProductRepository;
import com.smartprocure.domain.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;

    @Transactional(readOnly = true)
    public Page<InventoryDto> getAllInventory(Pageable pageable) {
        return inventoryRepository.findAll(pageable).map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public List<InventoryDto> getLowStockAlerts() {
        return inventoryRepository.findLowStockInventories().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public InventoryDto updateStock(Long productId, Long warehouseId, Integer quantityAdjustment) {
        Inventory inventory = inventoryRepository.findByProductIdAndWarehouseId(productId, warehouseId)
                .orElseGet(() -> {
                    Product product = productRepository.findById(productId)
                            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
                    Warehouse warehouse = warehouseRepository.findById(warehouseId)
                            .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", warehouseId));
                    return Inventory.builder()
                            .product(product)
                            .warehouse(warehouse)
                            .quantityOnHand(0)
                            .quantityAllocated(0)
                            .quantityAvailable(0)
                            .minStockLevel(5)
                            .build();
                });

        int newOnHand = Math.max(0, inventory.getQuantityOnHand() + quantityAdjustment);
        inventory.setQuantityOnHand(newOnHand);
        inventory.setQuantityAvailable(Math.max(0, newOnHand - inventory.getQuantityAllocated()));

        return mapToDto(inventoryRepository.save(inventory));
    }

    private InventoryDto mapToDto(Inventory inv) {
        boolean lowStock = inv.getQuantityAvailable() <= inv.getMinStockLevel();
        return InventoryDto.builder()
                .id(inv.getId())
                .productId(inv.getProduct().getId())
                .productName(inv.getProduct().getName())
                .productSku(inv.getProduct().getSku())
                .warehouseId(inv.getWarehouse().getId())
                .warehouseName(inv.getWarehouse().getName())
                .quantityOnHand(inv.getQuantityOnHand())
                .quantityAllocated(inv.getQuantityAllocated())
                .quantityAvailable(inv.getQuantityAvailable())
                .minStockLevel(inv.getMinStockLevel())
                .isLowStock(lowStock)
                .build();
    }
}
