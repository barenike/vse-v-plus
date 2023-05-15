package com.example.vse_back.model.repository;

import com.example.vse_back.model.entity.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, UUID> {
    @Query("select e from EventEntity e where e.id = ?1")
    EventEntity findByEventId(UUID id);
}
