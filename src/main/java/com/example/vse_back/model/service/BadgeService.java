package com.example.vse_back.model.service;

import com.example.vse_back.exceptions.EntityIsNotFoundException;
import com.example.vse_back.infrastructure.badge.BadgeCreationRequest;
import com.example.vse_back.infrastructure.badge.BadgeEditRequest;
import com.example.vse_back.model.entity.BadgeEntity;
import com.example.vse_back.model.entity.ImageEntity;
import com.example.vse_back.model.repository.BadgeRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
public class BadgeService {
    private final BadgeRepository badgeRepository;
    private final UserBadgeService userBadgeService;
    private final ImageService imageService;

    public BadgeService(BadgeRepository badgeRepository, UserBadgeService userBadgeService, ImageService imageService) {
        this.badgeRepository = badgeRepository;
        this.userBadgeService = userBadgeService;
        this.imageService = imageService;
    }

    public void createBadge(BadgeCreationRequest badgeCreationRequest) {
        BadgeEntity badge = new BadgeEntity();
        badge.setName(badgeCreationRequest.getName());
        badge.setDescription(badgeCreationRequest.getDescription());
        ImageEntity image = imageService.createAndGetImage(badgeCreationRequest.getFile());
        badge.setImage(image);
        badgeRepository.save(badge);
        userBadgeService.addNewBadgeToUsers(badge);
    }

    public void editBadge(BadgeEditRequest badgeEditRequest) {
        BadgeEntity badge = getBadgeById(badgeEditRequest.getBadgeId());
        badge.setName(badgeEditRequest.getName());
        badge.setDescription(badgeEditRequest.getDescription());
        badge.setImage(setupImage(badge, badgeEditRequest.getFile()));
        badgeRepository.save(badge);
    }

    private ImageEntity setupImage(BadgeEntity badge, MultipartFile file) {
        if (badge.getImage() != null) {
            imageService.deleteImage(badge.getImage().getId());
        }
        return imageService.createAndGetImage(file);
    }

    public boolean deleteBadgeById(UUID id) {
        if (badgeRepository.existsById(id)) {
            ImageEntity image = getBadgeById(id).getImage();
            userBadgeService.deleteUsersBadgesByBadgeId(id);
            badgeRepository.deleteById(id);
            imageService.deleteImage(image.getId());
            return true;
        }
        return false;
    }

    public BadgeEntity getBadgeById(UUID id) {
        BadgeEntity badge = badgeRepository.findByBadgeId(id);
        if (badge == null) {
            throw new EntityIsNotFoundException("badge", id);
        }
        return badge;
    }

    public List<BadgeEntity> getAllBadges() {
        return badgeRepository.findAll();
    }
}
