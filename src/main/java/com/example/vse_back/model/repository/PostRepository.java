package com.example.vse_back.model.repository;

import com.example.vse_back.model.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, UUID> {
    @Query("select p from PostEntity p where p.id = ?1")
    PostEntity findByPostId(UUID id);

    List<PostEntity> findByUserId(UUID id);
}
