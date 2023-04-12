package com.example.vse_back.infrastructure.product;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class ProductAmountRequest {
    @NotNull
    private String productId;

    @Min(1)
    @NotNull
    private Integer amount;
}
