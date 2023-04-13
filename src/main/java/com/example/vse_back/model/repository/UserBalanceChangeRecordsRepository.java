package com.example.vse_back.model.repository;

import com.example.vse_back.model.entity.UserBalanceChangeRecordsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserBalanceChangeRecordsRepository extends JpaRepository<UserBalanceChangeRecordsEntity, UUID> {
    List<UserBalanceChangeRecordsEntity> findByUserId(UUID userId);
}
