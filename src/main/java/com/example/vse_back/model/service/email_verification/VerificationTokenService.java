package com.example.vse_back.model.service.email_verification;

import com.example.vse_back.model.entity.UserEntity;
import com.example.vse_back.model.entity.VerificationTokenEntity;
import com.example.vse_back.model.repository.VerificationTokenRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class VerificationTokenService {
    private final VerificationTokenRepository verificationTokenRepository;

    public VerificationTokenService(VerificationTokenRepository verificationTokenRepository) {
        this.verificationTokenRepository = verificationTokenRepository;
    }

    public void createToken(UserEntity user, String token) {
        VerificationTokenEntity verificationToken = new VerificationTokenEntity();
        verificationToken.setUser(user);
        verificationToken.setToken(token);
        verificationTokenRepository.save(verificationToken);
    }

    public VerificationTokenEntity getToken(final String token) {
        return verificationTokenRepository.findByToken(token);
    }

    public boolean deleteByUserId(UUID userId) {
        VerificationTokenEntity token = verificationTokenRepository.findByUserId(userId);
        if (token != null) {
            verificationTokenRepository.deleteById(token.getId());
            return true;
        }
        return false;
    }
}
