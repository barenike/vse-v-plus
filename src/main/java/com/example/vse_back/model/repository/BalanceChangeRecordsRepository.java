package com.example.vse_back.model.repository;

import com.example.vse_back.model.entity.BalanceChangeRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BalanceChangeRecordsRepository extends JpaRepository<BalanceChangeRecordEntity, UUID> {
    List<BalanceChangeRecordEntity> findByObjectUserId(UUID userId);
}
