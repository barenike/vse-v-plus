package com.example.vse_back.infrastructure.product;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Data
public class ProductCreationRequest {
    @NotNull
    private String name;

    @NotNull
    private Integer price;

    private String description;

    @NotNull
    private Integer amount;

    @NotNull
    private MultipartFile file;
}
