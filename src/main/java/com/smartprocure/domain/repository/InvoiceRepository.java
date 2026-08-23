package com.smartprocure.domain.repository;

import com.smartprocure.domain.entity.Invoice;
import com.smartprocure.domain.enums.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    List<Invoice> findByStatus(InvoiceStatus status);
    List<Invoice> findByVendorId(Long vendorId);

    @Query("SELECT COALESCE(SUM(i.totalAmount - i.paidAmount), 0) FROM Invoice i WHERE i.status IN (com.smartprocure.domain.enums.InvoiceStatus.SUBMITTED, com.smartprocure.domain.enums.InvoiceStatus.APPROVED, com.smartprocure.domain.enums.InvoiceStatus.PARTIALLY_PAID)")
    BigDecimal calculatePendingInvoicesAmount();
}
