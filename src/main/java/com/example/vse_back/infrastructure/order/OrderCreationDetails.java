package com.example.vse_back.infrastructure.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderCreationDetails {
    @NotNull
    private String productId;

    @Min(1)
    @NotNull
    private Integer quantity;
}
