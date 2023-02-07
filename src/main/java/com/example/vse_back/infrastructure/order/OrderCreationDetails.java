package com.example.vse_back.infrastructure.order;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class OrderCreationDetails {
    @NotNull
    private String productId;

    @NotNull
    private Integer quantity;
}
