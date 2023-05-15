package com.example.vse_back.infrastructure.user;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class TransferCoinsRequest {
    @NotNull
    private UUID userId;

    @Min(1)
    @NotNull
    private Integer userBalance;

    @NotNull
    private String cause;
}
