package com.example.vse_back.model.repository;

import com.example.vse_back.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    UserEntity findByEmail(String email);

    @Query("select u from UserEntity u where u.id = ?1")
    UserEntity findByUserId(UUID userId);
}
