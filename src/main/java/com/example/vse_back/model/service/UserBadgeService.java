package com.example.vse_back.model.service;

import com.example.vse_back.exceptions.EntityIsNotFoundException;
import com.example.vse_back.infrastructure.user_badge.UserBadgeStatusChangeRequest;
import com.example.vse_back.model.entity.BadgeEntity;
import com.example.vse_back.model.entity.UserBadgeEntity;
import com.example.vse_back.model.entity.UserEntity;
import com.example.vse_back.model.repository.UserBadgeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserBadgeService {
    private final UserBadgeRepository userBadgeRepository;

    private final UserService userService;

    @Autowired
    private BadgeService badgeService;

    public UserBadgeService(UserBadgeRepository userBadgeRepository, UserService userService) {
        this.userBadgeRepository = userBadgeRepository;
        this.userService = userService;
    }

    public void addBadgesToNewUser(UserEntity user) {
        List<BadgeEntity> bagdeList = badgeService.getAllBadges();
        bagdeList.forEach(badge -> {
            UserBadgeEntity userBadge = new UserBadgeEntity();
            userBadge.setUser(user);
            userBadge.setBadge(badge);
            userBadge.setIsActivated(false);
            userBadgeRepository.save(userBadge);
        });
    }

    public void addNewBadgeToUsers(BadgeEntity badge) {
        List<UserEntity> userList = userService.getAllUsers();
        userList.forEach(user -> {
            UserBadgeEntity userBadge = new UserBadgeEntity();
            userBadge.setUser(user);
            userBadge.setBadge(badge);
            userBadge.setIsActivated(false);
            userBadgeRepository.save(userBadge);
        });
    }

    public UserBadgeEntity getUserBadgeById(UUID id) {
        UserBadgeEntity userBadge = userBadgeRepository.findByUserBadgeId(id);
        if (userBadge == null) {
            throw new EntityIsNotFoundException("user badge", id.toString());
        }
        return userBadge;
    }

    public List<UserBadgeEntity> getUserBadgesByUserId(UUID id) {
        return userBadgeRepository.findByUserId(id);
    }

    public void changeUserBadgeStatus(UserBadgeStatusChangeRequest request) {
        UserBadgeEntity userBadge = getUserBadgeById(UUID.fromString(request.getUserBadgeId()));
        userBadge.setIsActivated(request.isActivated());
        userBadgeRepository.save(userBadge);
    }

    public void deleteUsersBadgesByBadgeId(UUID id) {
        userBadgeRepository.deleteAllByBadgeId(id);
    }
}
