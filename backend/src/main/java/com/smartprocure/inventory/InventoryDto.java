package com.smartprocure.inventory;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryDto {

    private Long id;

    @NotNull(message = "Product ID is required")
    private Long productId;
    private String productName;
    private String productSku;

    @NotNull(message = "Warehouse ID is required")
    private Long warehouseId;
    private String warehouseName;

    private Integer quantityOnHand;
    private Integer quantityAllocated;
    private Integer quantityAvailable;
    private Integer minStockLevel;
    private Boolean isLowStock;
}
