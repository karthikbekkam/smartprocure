package com.smartprocure.domain.repository;

import com.smartprocure.domain.Vendor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {
    Optional<Vendor> findByTaxId(String taxId);
    Optional<Vendor> findByCompanyName(String companyName);
    Page<Vendor> findByStatus(String status, Pageable pageable);
    long countByStatus(String status);
}
