package com.smartprocure.modules.inventory.repository;

import com.smartprocure.modules.inventory.entity.StockMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA Repository for Stock Movement audit ledger records.
 *
 * @author Principal Java Architect
 */
@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    Page<StockMovement> findByProductId(Long productId, Pageable pageable);

    Page<StockMovement> findBySourceWarehouseIdOrDestinationWarehouseId(Long sourceId, Long destId, Pageable pageable);
}
