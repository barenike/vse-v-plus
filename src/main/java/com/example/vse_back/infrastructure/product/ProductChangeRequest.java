package com.example.vse_back.infrastructure.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Data
public class ProductChangeRequest {
    @NotNull
    private UUID productId;

    @NotNull
    private String name;

    @Min(1)
    @NotNull
    private Integer price;

    private String description;

    @Min(1)
    @NotNull
    private Integer amount;

    @NotNull
    private MultipartFile file;
}

