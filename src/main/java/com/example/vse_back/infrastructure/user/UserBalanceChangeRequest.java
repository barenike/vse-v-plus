package com.example.vse_back.infrastructure.user;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class UserBalanceChangeRequest {
    @NotNull
    private UUID userId;

    @Min(0)
    @NotNull
    private Integer userBalance;

    @NotNull
    private String cause;
}
