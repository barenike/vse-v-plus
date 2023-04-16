package com.example.vse_back.infrastructure.posts;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PostCreationRequest {
    private String title;

    private String text;

    private MultipartFile file;
}
