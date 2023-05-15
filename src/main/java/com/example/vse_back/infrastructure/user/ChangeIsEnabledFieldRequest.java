package com.example.vse_back.infrastructure.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ChangeIsEnabledFieldRequest {
    @NotNull
    private UUID userId;

    @NotNull
    private boolean isEnabled;
}
