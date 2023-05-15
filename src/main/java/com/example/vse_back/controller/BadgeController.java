package com.example.vse_back.controller;

import com.example.vse_back.infrastructure.badge.BadgeCreationRequest;
import com.example.vse_back.infrastructure.badge.BadgeEditRequest;
import com.example.vse_back.model.entity.BadgeEntity;
import com.example.vse_back.model.service.BadgeService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class BadgeController {
    private final BadgeService badgeService;

    public BadgeController(BadgeService badgeService) {
        this.badgeService = badgeService;
    }

    @Operation(summary = "Create a badge")
    @PostMapping("/admin/badge/create")
    public ResponseEntity<Object> createBadge(@ModelAttribute @Valid BadgeCreationRequest badgeCreationRequest) {
        badgeService.createBadge(badgeCreationRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "Edit the badge")
    @PostMapping("/admin/badge/edit")
    public ResponseEntity<Object> editBadge(@ModelAttribute @Valid BadgeEditRequest badgeEditRequest) {
        badgeService.editBadge(badgeEditRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Delete the badge")
    @DeleteMapping("/admin/badge/{badgeId}")
    public ResponseEntity<Object> deleteBadge(@PathVariable(name = "badgeId") UUID badgeId) {
        final boolean isDeleted = badgeService.deleteBadgeById(badgeId);
        return isDeleted
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
    }

    @Operation(summary = "Get all badges")
    @GetMapping("/admin/badges")
    public ResponseEntity<List<BadgeEntity>> getAllBadges() {
        final List<BadgeEntity> badges = badgeService.getAllBadges();
        return badges != null && !badges.isEmpty()
                ? new ResponseEntity<>(badges, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.OK);
    }
}
