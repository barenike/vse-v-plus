package com.example.vse_back.model.repository;

import com.example.vse_back.model.entity.OrderRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRecordRepository extends JpaRepository<OrderRecordEntity, UUID> {
    List<OrderRecordEntity> findByOrderId(UUID orderId);
}
