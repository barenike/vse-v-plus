package com.example.vse_back.infrastructure.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductAmountRequest {
    @NotNull
    private String productId;

    @Min(1)
    @NotNull
    private Integer amount;
}
