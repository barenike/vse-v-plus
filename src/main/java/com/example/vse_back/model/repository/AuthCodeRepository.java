package com.example.vse_back.model.repository;

import com.example.vse_back.model.entity.AuthCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AuthCodeRepository extends JpaRepository<AuthCodeEntity, UUID> {
    AuthCodeEntity findByUserId(UUID userId);
}
