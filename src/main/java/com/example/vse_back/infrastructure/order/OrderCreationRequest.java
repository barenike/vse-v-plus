package com.example.vse_back.infrastructure.order;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class OrderCreationRequest {
    @NotNull
    @NotEmpty
    List<OrderCreationDetails> orderCreationDetails;
}
