package com.smartprocure.domain.repository;

import com.smartprocure.domain.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    Page<AuditLog> findByModule(String module, Pageable pageable);
    Page<AuditLog> findByPerformedById(Long userId, Pageable pageable);
}
