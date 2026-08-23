package com.smartprocure.modules.inventory.entity;

import com.smartprocure.core.entity.BaseEntity;
import com.smartprocure.modules.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;

/**
 * Enterprise Warehouse Master Entity representing distribution centers, storage facilities, and stock yards.
 *
 * @author Principal Java Architect
 */
@Entity
@Table(name = "warehouses", indexes = {
        @Index(name = "idx_warehouse_code", columnList = "code", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Warehouse extends BaseEntity {

    @Column(name = "code", nullable = false, unique = true, length = 30)
    private String code;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "location", length = 250)
    private String location;

    @Column(name = "capacity")
    private Double capacity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private User manager;
}
