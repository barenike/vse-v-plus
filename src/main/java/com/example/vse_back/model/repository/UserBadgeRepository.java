package com.example.vse_back.model.repository;

import com.example.vse_back.model.entity.UserBadgeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserBadgeRepository extends JpaRepository<UserBadgeEntity, Integer> {
    List<UserBadgeEntity> findByUserId(UUID id);

    void deleteAllByBadgeId(UUID badgeId);

    @Query("select u from UserBadgeEntity u where u.id = ?1")
    UserBadgeEntity findByUserBadgeId(UUID id);
}
