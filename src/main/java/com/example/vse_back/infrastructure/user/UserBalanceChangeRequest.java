package com.example.vse_back.infrastructure.user;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserBalanceChangeRequest {
    @NotNull
    private String userId;

    @Min(0)
    @NotNull
    private Integer userBalance;

    @NotNull
    private String cause;
}
