package com.smartprocure.domain.repository;

import com.smartprocure.domain.entity.StockTransfer;
import com.smartprocure.domain.enums.TransferStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockTransferRepository extends JpaRepository<StockTransfer, Long> {
    Optional<StockTransfer> findByTransferNumber(String transferNumber);
    List<StockTransfer> findByStatus(TransferStatus status);
    List<StockTransfer> findBySourceWarehouseIdOrTargetWarehouseId(Long sourceId, Long targetId);
}
