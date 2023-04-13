package com.example.vse_back.model.repository;

import com.example.vse_back.model.entity.PasswordResetTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetTokenEntity, UUID> {
    PasswordResetTokenEntity findByToken(String token);

    PasswordResetTokenEntity findByUserId(UUID userId);
}
