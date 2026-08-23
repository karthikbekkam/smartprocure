package com.smartprocure.modules.auth.repository;

import com.smartprocure.modules.auth.entity.Role;
import com.smartprocure.modules.auth.enums.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA Repository for Role Entity.
 *
 * @author Principal Java Architect
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
