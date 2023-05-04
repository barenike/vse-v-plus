package com.example.vse_back.infrastructure.user_badge;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserBadgeStatusChangeRequest {
    @NotNull
    private String userBadgeId;

    @NotNull
    private boolean isActivated;
}