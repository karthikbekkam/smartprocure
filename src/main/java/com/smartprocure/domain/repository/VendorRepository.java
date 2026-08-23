package com.smartprocure.domain.repository;

import com.smartprocure.domain.entity.Vendor;
import com.smartprocure.domain.enums.VendorStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {
    Optional<Vendor> findByTaxId(String taxId);
    Optional<Vendor> findByCompanyName(String companyName);
    List<Vendor> findByStatus(VendorStatus status);
    Optional<Vendor> findByUserId(Long userId);
    
    @Query("SELECT v FROM Vendor v WHERE (:keyword IS NULL OR LOWER(v.companyName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(v.taxId) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND (:status IS NULL OR v.status = :status)")
    List<Vendor> searchVendors(@Param("keyword") String keyword, @Param("status") VendorStatus status);
    
    long countByStatus(VendorStatus status);
}
