package com.example.vse_back.infrastructure.user_badge;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class UserBadgeStatusChangeRequest {
    @NotNull
    private UUID userBadgeId;

    @NotNull
    private boolean isActivated;
}