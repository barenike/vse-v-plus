package com.example.vse_back.infrastructure.posts;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PostCreationRequest {
    @NotNull
    private String title;

    @NotNull
    private String text;

    @NotNull
    private MultipartFile file;
}
