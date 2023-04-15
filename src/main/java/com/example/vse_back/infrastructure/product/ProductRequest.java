package com.example.vse_back.infrastructure.product;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductRequest {
    @NotNull
    private String productId;
}
