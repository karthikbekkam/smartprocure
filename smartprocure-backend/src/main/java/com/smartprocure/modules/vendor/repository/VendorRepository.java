package com.smartprocure.modules.vendor.repository;

import com.smartprocure.modules.vendor.entity.Vendor;
import com.smartprocure.modules.vendor.enums.VendorStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA Repository for Vendor Entity supporting dynamic specification searching & pagination.
 *
 * @author Principal Java Architect
 */
@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long>, JpaSpecificationExecutor<Vendor> {

    Optional<Vendor> findByVendorCode(String vendorCode);

    Optional<Vendor> findByEmail(String email);

    Optional<Vendor> findByTaxId(String taxId);

    Boolean existsByTaxId(String taxId);

    Boolean existsByEmail(String email);

    Page<Vendor> findByStatus(VendorStatus status, Pageable pageable);
}
