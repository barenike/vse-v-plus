package com.example.vse_back.model.repository;

import com.example.vse_back.model.entity.AuthTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AuthTokenRepository extends JpaRepository<AuthTokenEntity, UUID> {
    AuthTokenEntity findByToken(String token);

    AuthTokenEntity findByUserId(UUID userId);
}
