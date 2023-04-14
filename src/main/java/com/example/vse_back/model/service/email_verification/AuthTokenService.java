package com.example.vse_back.model.service.email_verification;

import com.example.vse_back.exceptions.AuthTokenIsNotFoundException;
import com.example.vse_back.model.entity.AuthTokenEntity;
import com.example.vse_back.model.entity.UserEntity;
import com.example.vse_back.model.repository.AuthTokenRepository;
import com.example.vse_back.utils.Util;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthTokenService {
    private final AuthTokenRepository authTokenRepository;

    public AuthTokenService(AuthTokenRepository authTokenRepository) {
        this.authTokenRepository = authTokenRepository;
    }

    public void createToken(UserEntity user, String token) {
        AuthTokenEntity authToken = new AuthTokenEntity();
        authToken.setUser(user);
        authToken.setToken(token);
        authToken.setCreationDate(Util.getCurrentMoscowDate());
        authTokenRepository.save(authToken);
    }

    public AuthTokenEntity getToken(String token) {
        AuthTokenEntity authToken = authTokenRepository.findByToken(token);
        if (authToken == null) {
            throw new AuthTokenIsNotFoundException(token);
        }
        return authToken;
    }

    // Only for tests
    public AuthTokenEntity getTokenBuUserId(UUID userId) {
        return authTokenRepository.findByUserId(userId);
    }

    public boolean deleteByUserId(UUID userId) {
        AuthTokenEntity authToken = authTokenRepository.findByUserId(userId);
        if (authToken != null) {
            authTokenRepository.deleteById(authToken.getId());
            return true;
        }
        return false;
    }

    public void deleteByToken(String token) {
        AuthTokenEntity authToken = authTokenRepository.findByToken(token);
        if (authToken != null) {
            authTokenRepository.deleteById(authToken.getId());
        }
    }
}
