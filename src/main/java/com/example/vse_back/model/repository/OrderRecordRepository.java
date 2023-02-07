package com.example.vse_back.model.repository;

import com.example.vse_back.model.entity.OrderRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderRecordRepository extends JpaRepository<OrderRecordEntity, UUID> {
    List<OrderRecordEntity> findByOrderId(UUID orderId);
}
