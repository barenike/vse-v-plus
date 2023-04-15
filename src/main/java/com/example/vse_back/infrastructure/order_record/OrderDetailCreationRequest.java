package com.example.vse_back.infrastructure.order_record;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

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
