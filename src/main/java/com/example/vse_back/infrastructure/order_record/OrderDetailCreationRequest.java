package com.example.vse_back.infrastructure.order_record;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderDetailCreationRequest {
    @NotNull
    private String orderId;

    @NotNull
    private String productId;

    @Min(1)
    @NotNull
    private Integer quantity;
}
