package com.smartprocure.modules.finance.repository;

import com.smartprocure.modules.finance.entity.Invoice;
import com.smartprocure.modules.finance.enums.InvoiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA Repository for Invoice Entity.
 *
 * @author Principal Java Architect
 */
@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long>, JpaSpecificationExecutor<Invoice> {

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    Page<Invoice> findByVendorId(Long vendorId, Pageable pageable);

    Page<Invoice> findByStatus(InvoiceStatus status, Pageable pageable);
}
