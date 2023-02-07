package com.example.vse_back.model.service;

import com.example.vse_back.model.entity.PasswordResetTokenEntity;
import com.example.vse_back.model.entity.UserEntity;
import com.example.vse_back.model.repository.PasswordResetTokenRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PasswordResetTokenService {
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final JavaMailSender mailSender;

    public PasswordResetTokenService(PasswordResetTokenRepository passwordResetTokenRepository, JavaMailSender mailSender) {
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.mailSender = mailSender;
    }

    public PasswordResetTokenEntity validatePasswordResetToken(String token) {
        return getToken(token);
    }

    public void createToken(UserEntity user, String token) {
        PasswordResetTokenEntity passwordResetToken = new PasswordResetTokenEntity();
        passwordResetToken.setUser(user);
        passwordResetToken.setToken(token);
        passwordResetTokenRepository.save(passwordResetToken);
    }

    public PasswordResetTokenEntity getToken(final String token) {
        return passwordResetTokenRepository.findByToken(token);
    }

    public void deleteByUserId(UUID userId) {
        PasswordResetTokenEntity token = passwordResetTokenRepository.findByUserId(userId);
        if (token != null) {
            passwordResetTokenRepository.deleteById(token.getId());
        }
    }

    public void resetPassword(UserEntity user, String appUrl) {
        final String token = UUID.randomUUID().toString();
        this.createToken(user, token);
        SimpleMailMessage email = constructEmail(user, token, appUrl);
        mailSender.send(email);
    }

    private SimpleMailMessage constructEmail(UserEntity user, String token, String appUrl) {
        String recipientAddress = user.getEmail();
        String subject = "Смена пароля аккаунта на портале VSE В ПЛЮСЕ";
        String confirmationUrl = appUrl + "/reset/password/" + token;
        String message = "Здравствуйте! Откройте эту ссылку, если вы хотите сменить пароль. Если вы не отправляли запрос на смену пароля - проигнорируйте это письмо.";
        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom("no-reply-vse-vplus@mail.ru");
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message + " \r\n" + confirmationUrl);
        return email;
    }
}
