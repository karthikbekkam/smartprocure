package com.smartprocure.domain.repository;

import com.smartprocure.domain.entity.PurchaseRequest;
import com.smartprocure.domain.enums.PurchaseRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseRequestRepository extends JpaRepository<PurchaseRequest, Long> {
    Optional<PurchaseRequest> findByPrNumber(String prNumber);
    List<PurchaseRequest> findByStatus(PurchaseRequestStatus status);
    List<PurchaseRequest> findByRequestedById(Long userId);
}
