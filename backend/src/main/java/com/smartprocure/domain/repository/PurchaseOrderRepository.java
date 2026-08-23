package com.smartprocure.domain.repository;

import com.smartprocure.domain.PurchaseOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    Optional<PurchaseOrder> findByPoNumber(String poNumber);
    Page<PurchaseOrder> findByStatus(String status, Pageable pageable);
    Page<PurchaseOrder> findByVendorId(Long vendorId, Pageable pageable);
    long countByStatus(String status);

    @Query("SELECT SUM(po.totalAmount) FROM PurchaseOrder po WHERE po.status = 'COMPLETED' OR po.status = 'APPROVED'")
    BigDecimal calculateTotalProcurementValue();
}
