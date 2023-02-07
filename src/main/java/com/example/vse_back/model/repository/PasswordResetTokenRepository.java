package com.example.vse_back.model.repository;

import com.example.vse_back.model.entity.PasswordResetTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetTokenEntity, UUID> {
    PasswordResetTokenEntity findByToken(String token);

    PasswordResetTokenEntity findByUserId(UUID userId);
}
