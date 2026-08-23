package com.smartprocure.modules.audit.repository;

import com.smartprocure.modules.audit.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA Repository for AuditLog Entity.
 *
 * @author Principal Java Architect
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    Page<AuditLog> findByUserEmail(String userEmail, Pageable pageable);

    Page<AuditLog> findByAction(String action, Pageable pageable);
}
