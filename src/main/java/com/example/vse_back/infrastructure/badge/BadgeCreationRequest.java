package com.example.vse_back.infrastructure.badge;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class BadgeCreationRequest {
    @NotNull
    private String name;

    private String description;

    @NotNull
    private MultipartFile file;
}