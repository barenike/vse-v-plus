package com.example.vse_back.infrastructure.badge;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Data
public class BadgeEditRequest {
    @NotNull
    private UUID badgeId;

    @NotNull
    private String name;

    private String description;

    @NotNull
    private MultipartFile file;
}