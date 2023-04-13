package com.example.vse_back.infrastructure.user;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class UserBalanceRequest {
    @NotNull
    private String userId;

    @Min(0)
    @NotNull
    private Integer userBalance;

    @NotNull
    private String cause;
}
