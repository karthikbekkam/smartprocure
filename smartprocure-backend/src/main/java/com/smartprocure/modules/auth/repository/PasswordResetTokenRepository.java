package com.smartprocure.modules.auth.repository;

import com.smartprocure.modules.auth.entity.PasswordResetToken;
import com.smartprocure.modules.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA Repository for PasswordResetToken Entity.
 *
 * @author Principal Java Architect
 */
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByUser(User user);
}
