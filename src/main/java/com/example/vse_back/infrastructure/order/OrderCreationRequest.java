package com.example.vse_back.infrastructure.order;

import com.example.vse_back.infrastructure.order_detail.OrderCreationDetails;
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
