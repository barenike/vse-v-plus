package com.example.vse_back.infrastructure.order_detail;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class OrderCreationDetails {
    @NotNull
    private UUID productId;

    @Min(1)
    @NotNull
    private Integer quantity;
}
