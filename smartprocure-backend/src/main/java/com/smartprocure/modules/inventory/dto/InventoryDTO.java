package com.smartprocure.modules.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Inventory stock snapshot display.
 *
 * @author Principal Java Architect
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryDTO {

    private Long id;

    private Long productId;
    private String productSku;
    private String productName;

    private Long warehouseId;
    private String warehouseCode;
    private String warehouseName;

    private Integer quantityOnHand;
    private Integer allocatedQuantity;
    private Integer availableQuantity;
}
