package com.example.vse_back.infrastructure.posts;

import com.example.vse_back.model.entity.ImageEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PostResponse {
    private String id;

    private String title;

    private String text;

    private LocalDateTime date;

    private String userId;

    private ImageEntity image;
}
