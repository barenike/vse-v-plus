package com.example.vse_back.infrastructure.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangeIsEnabledFieldRequest {
    @NotNull
    private String userId;

    @NotNull
    private boolean isEnabled;
}
