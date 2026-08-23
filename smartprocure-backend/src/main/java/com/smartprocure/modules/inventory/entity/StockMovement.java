package com.smartprocure.modules.inventory.entity;

import com.smartprocure.core.entity.BaseEntity;
import com.smartprocure.modules.inventory.enums.StockMovementType;
import com.smartprocure.modules.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

/**
 * Enterprise Stock Movement Audit Ledger Entity recording all inventory transactions.
 *
 * @author Principal Java Architect
 */
@Entity
@Table(name = "stock_movements", indexes = {
        @Index(name = "idx_movement_number", columnList = "movement_number", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockMovement extends BaseEntity {

    @Column(name = "movement_number", nullable = false, unique = true, length = 50)
    private String movementNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_warehouse_id")
    private Warehouse sourceWarehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_warehouse_id")
    private Warehouse destinationWarehouse;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false, length = 30)
    private StockMovementType movementType;

    @Column(name = "reference_document", length = 100)
    private String referenceDocument;

    @Column(name = "remarks", length = 250)
    private String remarks;
}
