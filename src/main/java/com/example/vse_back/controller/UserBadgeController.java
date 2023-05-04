package com.example.vse_back.controller;

import com.example.vse_back.infrastructure.user_badge.UserBadgeStatusChangeRequest;
import com.example.vse_back.model.entity.UserBadgeEntity;
import com.example.vse_back.model.entity.UserEntity;
import com.example.vse_back.model.service.UserBadgeService;
import com.example.vse_back.model.service.utils.LocalUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class UserBadgeController {
    private final UserBadgeService userBadgeService;
    private final LocalUtil localUtil;

    public UserBadgeController(UserBadgeService userBadgeService, LocalUtil localUtil) {
        this.userBadgeService = userBadgeService;
        this.localUtil = localUtil;
    }

    @Operation(summary = "Get my user badges")
    @GetMapping("/user/user_badges")
    public ResponseEntity<List<UserBadgeEntity>> getMyUserBadges(@RequestHeader(name = "Authorization") String token) {
        UserEntity user = localUtil.getUserFromToken(token);
        final List<UserBadgeEntity> userBadges = userBadgeService.getUserBadgesByUserId(user.getId());
        return userBadges != null && !userBadges.isEmpty()
                ? new ResponseEntity<>(userBadges, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Get badges of the user")
    @GetMapping("/user_badges/{userId}")
    public ResponseEntity<List<UserBadgeEntity>> getUserBadges(@PathVariable(name = "userId") UUID userId) {
        final List<UserBadgeEntity> userBadges = userBadgeService.getUserBadgesByUserId(userId);
        return userBadges != null && !userBadges.isEmpty()
                ? new ResponseEntity<>(userBadges, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Activate/deactivate user's badge")
    @PostMapping("/admin/user_badge")
    public ResponseEntity<Object> changeIsActivatedFieldInUserBadge(@RequestBody @Valid UserBadgeStatusChangeRequest request) {
        userBadgeService.changeUserBadgeStatus(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
