package com.example.vse_back.infrastructure.post;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Data
public class PostEditRequest {
    @NotNull
    private UUID postId;

    @NotNull
    private String title;

    @NotNull
    private String text;

    private MultipartFile file;
}
