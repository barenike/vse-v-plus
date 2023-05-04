package com.example.vse_back.controller;

import com.example.vse_back.model.entity.UserBadgeEntity;
import com.example.vse_back.model.entity.UserEntity;
import com.example.vse_back.model.service.UserBadgeService;
import com.example.vse_back.model.service.utils.LocalUtil;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

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
    @GetMapping("/user/user_badges/{userId}")
    public ResponseEntity<List<UserBadgeEntity>> getUserBadges(@PathVariable(name = "userId") UUID userId) {
        final List<UserBadgeEntity> userBadges = userBadgeService.getUserBadgesByUserId(userId);
        return userBadges != null && !userBadges.isEmpty()
                ? new ResponseEntity<>(userBadges, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.OK);
    }

    // And what about deactivation?
    @Operation(summary = "Activate user's badge")
    @GetMapping("/admin/user_badge/{userBadgeId}")
    public ResponseEntity<Object> activateUserBadge(@PathVariable(name = "userBadgeId") UUID userBadgeId) {
        userBadgeService.activateUserBadge(userBadgeId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
