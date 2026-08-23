package com.smartprocure.domain.repository;

import com.smartprocure.domain.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByEntityNameAndEntityId(String entityName, Long entityId);
    List<AuditLog> findByPerformedById(Long userId);
    List<AuditLog> findTop50ByOrderByTimestampDesc();
}
