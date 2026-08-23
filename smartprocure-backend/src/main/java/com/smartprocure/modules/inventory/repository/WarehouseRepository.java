package com.smartprocure.modules.inventory.repository;

import com.smartprocure.modules.inventory.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA Repository for Warehouse Entity.
 *
 * @author Principal Java Architect
 */
@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    Optional<Warehouse> findByCode(String code);

    Boolean existsByCode(String code);
}
