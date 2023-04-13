package com.example.vse_back.model.repository;

import com.example.vse_back.model.entity.VerificationTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationTokenEntity, UUID> {
    VerificationTokenEntity findByToken(String token);

    VerificationTokenEntity findByUserId(UUID userId);
}
