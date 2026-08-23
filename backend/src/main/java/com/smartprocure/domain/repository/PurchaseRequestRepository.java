package com.smartprocure.domain.repository;

import com.smartprocure.domain.PurchaseRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PurchaseRequestRepository extends JpaRepository<PurchaseRequest, Long> {
    Optional<PurchaseRequest> findByPrNumber(String prNumber);
    Page<PurchaseRequest> findByStatus(String status, Pageable pageable);
    long countByStatus(String status);
}
