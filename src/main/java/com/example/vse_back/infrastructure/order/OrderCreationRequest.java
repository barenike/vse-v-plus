package com.example.vse_back.infrastructure.order;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class OrderCreationRequest {
    @NotNull
    @NotEmpty
    List<OrderCreationDetails> orderCreationDetails;
}
