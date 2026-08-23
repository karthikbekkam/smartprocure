package com.smartprocure.domain.repository;

import com.smartprocure.domain.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    Page<Invoice> findByStatus(String status, Pageable pageable);
    Page<Invoice> findByVendorId(Long vendorId, Pageable pageable);
    long countByStatus(String status);
}
