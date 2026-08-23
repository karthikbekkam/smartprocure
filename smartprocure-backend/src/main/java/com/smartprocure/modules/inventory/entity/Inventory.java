package com.smartprocure.modules.inventory.entity;

import com.smartprocure.core.entity.BaseEntity;
import com.smartprocure.modules.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

/**
 * Enterprise Inventory Stock Level Ledger Entity per product and warehouse location.
 *
 * @author Principal Java Architect
 */
@Entity
@Table(name = "inventories", uniqueConstraints = {
        @UniqueConstraint(name = "uk_product_warehouse", columnNames = {"product_id", "warehouse_id"})
})
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

    @Column(name = "quantity_on_hand", nullable = false)
    @Builder.Default
    private Integer quantityOnHand = 0;

    @Column(name = "allocated_quantity", nullable = false)
    @Builder.Default
    private Integer allocatedQuantity = 0;

    @Column(name = "available_quantity", nullable = false)
    @Builder.Default
    private Integer availableQuantity = 0;

    public void recalculateAvailableQuantity() {
        this.availableQuantity = this.quantityOnHand - this.allocatedQuantity;
    }
}
