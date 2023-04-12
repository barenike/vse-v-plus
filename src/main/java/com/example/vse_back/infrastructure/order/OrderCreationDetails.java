package com.example.vse_back.infrastructure.order;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class OrderCreationDetails {
    @NotNull
    private String productId;

    @Min(1)
    @NotNull
    private Integer quantity;
}
