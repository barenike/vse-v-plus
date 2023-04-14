package com.example.vse_back.model.service.email_verification;

import com.example.vse_back.exceptions.TooManyAuthAttemptsException;
import com.example.vse_back.model.entity.AuthCodeEntity;
import com.example.vse_back.model.entity.UserEntity;
import com.example.vse_back.model.repository.AuthCodeRepository;
import com.example.vse_back.utils.Util;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthCodeService {
    private final AuthCodeRepository authCodeRepository;

    public AuthCodeService(AuthCodeRepository authCodeRepository) {
        this.authCodeRepository = authCodeRepository;
    }

    public void createCode(UserEntity user, String code) {
        AuthCodeEntity authCode = new AuthCodeEntity();
        authCode.setUser(user);
        authCode.setCode(code);
        authCode.setCreationDate(Util.getCurrentMoscowDate());
        authCode.setAttemptCount(0);
        authCodeRepository.save(authCode);
    }

    // Null-unsafe
    public AuthCodeEntity getAuthCodeByUserId(UUID userId) {
        return authCodeRepository.findByUserId(userId);
    }

    public void incrementAttemptCount(AuthCodeEntity authCode) {
        Integer attemptCount = authCode.getAttemptCount();
        if (attemptCount > 2) {
            deleteByUserId(authCode.getUser().getId());
            throw new TooManyAuthAttemptsException();
        }
        authCode.setAttemptCount(attemptCount + 1);
        authCodeRepository.save(authCode);
    }

    public boolean deleteByUserId(UUID userId) {
        AuthCodeEntity authCode = authCodeRepository.findByUserId(userId);
        if (authCode != null) {
            authCodeRepository.deleteById(authCode.getId());
            return true;
        }
        return false;
    }
}
