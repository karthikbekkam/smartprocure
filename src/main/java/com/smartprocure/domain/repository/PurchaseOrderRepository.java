package com.smartprocure.domain.repository;

import com.smartprocure.domain.entity.PurchaseOrder;
import com.smartprocure.domain.enums.PurchaseOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    Optional<PurchaseOrder> findByPoNumber(String poNumber);
    Optional<PurchaseOrder> findByPurchaseRequestId(Long purchaseRequestId);
    List<PurchaseOrder> findByStatus(PurchaseOrderStatus status);
    List<PurchaseOrder> findByVendorId(Long vendorId);
    
    long countByStatus(PurchaseOrderStatus status);

    @Query("SELECT COALESCE(SUM(po.totalAmount), 0) FROM PurchaseOrder po WHERE po.status IN (com.smartprocure.domain.enums.PurchaseOrderStatus.ISSUED, com.smartprocure.domain.enums.PurchaseOrderStatus.CONFIRMED, com.smartprocure.domain.enums.PurchaseOrderStatus.RECEIVED)")
    BigDecimal calculateTotalSpend();
}
