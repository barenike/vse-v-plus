package com.example.vse_back.infrastructure.post;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PostCreationRequest {
    @NotNull
    private String title;

    @NotNull
    private String text;

    private MultipartFile file;
}
