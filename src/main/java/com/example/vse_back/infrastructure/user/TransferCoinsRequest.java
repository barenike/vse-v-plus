package com.example.vse_back.infrastructure.user;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TransferCoinsRequest {
    @NotNull
    private String userId;

    @Min(1)
    @NotNull
    private Integer userBalance;

    @NotNull
    private String cause;
}
