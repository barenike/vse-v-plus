package com.example.vse_back.model.service.utils;

import com.example.vse_back.configuration.jwt.JwtProvider;
import com.example.vse_back.model.entity.UserEntity;
import com.example.vse_back.model.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
public class LocalUtil {
    private final UserService userService;
    private final JwtProvider jwtProvider;

    public LocalUtil(UserService userService, JwtProvider jwtProvider) {
        this.userService = userService;
        this.jwtProvider = jwtProvider;
    }

    public static LocalDateTime getCurrentMoscowDate() {
        ZoneId zone = ZoneId.of("Europe/Moscow");
        ZonedDateTime date = ZonedDateTime.now(zone);
        return date.toLocalDateTime();
    }

    public UserEntity getUserFromToken(String token) {
        String userId = jwtProvider.getUserIdFromRawToken(token);
        return userService.getUserById(userId);
    }
}
