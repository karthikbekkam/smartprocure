package com.smartprocure.modules.auth.repository;

import com.smartprocure.modules.auth.entity.User;
import com.smartprocure.modules.auth.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA Repository for User Entity.
 *
 * @author Principal Java Architect
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
    Optional<User> findByEmailAndStatus(String email, UserStatus status);
}
