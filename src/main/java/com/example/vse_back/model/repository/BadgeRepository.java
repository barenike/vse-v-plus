package com.example.vse_back.model.repository;

import com.example.vse_back.model.entity.BadgeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BadgeRepository extends JpaRepository<BadgeEntity, UUID> {
    @Query("select b from BadgeEntity b where b.id = ?1")
    BadgeEntity findByBadgeId(UUID id);
}
