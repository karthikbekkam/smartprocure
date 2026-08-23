package com.smartprocure.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "inventory",
    uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "warehouse_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Builder.Default
    @Column(name = "quantity_on_hand", nullable = false)
    private Integer quantityOnHand = 0;

    @Builder.Default
    @Column(name = "quantity_allocated", nullable = false)
    private Integer quantityAllocated = 0;

    @Builder.Default
    @Column(name = "quantity_available", nullable = false)
    private Integer quantityAvailable = 0;

    @Builder.Default
    @Column(name = "min_stock_level")
    private Integer minStockLevel = 5;
}
