package com.example.vse_back.model.service.email_verification;

import com.example.vse_back.exceptions.TooManyAuthAttemptsException;
import com.example.vse_back.model.entity.AuthCodeEntity;
import com.example.vse_back.model.entity.UserEntity;
import com.example.vse_back.model.repository.AuthCodeRepository;
import com.example.vse_back.model.service.utils.LocalUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthCodeService {
    private final AuthCodeRepository authCodeRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthCodeService(AuthCodeRepository authCodeRepository, PasswordEncoder passwordEncoder) {
        this.authCodeRepository = authCodeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void createCode(UserEntity user, String code) {
        AuthCodeEntity authCode = new AuthCodeEntity();
        authCode.setId(user.getId());
        authCode.setCode(passwordEncoder.encode(code));
        authCode.setDate(LocalUtil.getCurrentMoscowDate());
        authCode.setAttemptCount(0);
        authCodeRepository.save(authCode);
    }

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

    public void deleteByUserId(UUID userId) {
        AuthCodeEntity authCode = authCodeRepository.findByUserId(userId);
        if (authCode != null) {
            authCodeRepository.deleteById(authCode.getId());
        }
    }
}
