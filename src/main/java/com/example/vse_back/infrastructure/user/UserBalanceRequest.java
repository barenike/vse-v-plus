package com.example.vse_back.infrastructure.user;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UserBalanceRequest {
    @NotNull
    private String userId;

    @NotNull
    private Integer userBalance;
}
