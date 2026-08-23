package com.smartprocure.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponseDTO {
    private Long id;
    private String inventoryCode;
    private Long productId;
    private String productSku;
    private String productName;
    private Long warehouseId;
    private String warehouseCode;
    private String warehouseName;
    private String warehouseLocation;
    private Integer quantityOnHand;
    private Integer quantityAllocated;
    private Integer quantityAvailable;
    private Integer minStockLevel;
    private String stockAlert;
    private LocalDateTime updatedAt;
}
