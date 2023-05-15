package com.example.vse_back.model.repository;

import com.example.vse_back.model.entity.BalanceChangeRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BalanceChangeRecordRepository extends JpaRepository<BalanceChangeRecordEntity, UUID> {
    @Query("select b from BalanceChangeRecordEntity b where b.objectUser.id = ?1 or b.subjectUser.id = ?1")
    List<BalanceChangeRecordEntity> findByUserId(UUID userId);
}
