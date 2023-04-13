package com.example.vse_back.model.repository;

import com.example.vse_back.model.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {
    @Query("select p from ProductEntity p where p.id = ?1")
    ProductEntity findByProductId(UUID id);
}
