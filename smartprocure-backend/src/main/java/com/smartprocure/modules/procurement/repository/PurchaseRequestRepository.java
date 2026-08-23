package com.smartprocure.modules.procurement.repository;

import com.smartprocure.modules.procurement.entity.PurchaseRequest;
import com.smartprocure.modules.procurement.enums.PurchaseRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA Repository for PurchaseRequest Entity.
 *
 * @author Principal Java Architect
 */
@Repository
public interface PurchaseRequestRepository extends JpaRepository<PurchaseRequest, Long>, JpaSpecificationExecutor<PurchaseRequest> {

    Optional<PurchaseRequest> findByRequestNumber(String requestNumber);

    Page<PurchaseRequest> findByRequestedById(Long userId, Pageable pageable);

    Page<PurchaseRequest> findByStatus(PurchaseRequestStatus status, Pageable pageable);
}
