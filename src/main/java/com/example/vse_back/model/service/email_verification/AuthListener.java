package com.example.vse_back.model.service.email_verification;

import com.example.vse_back.model.entity.UserEntity;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AuthListener implements ApplicationListener<OnRegistrationCompleteEvent> {
    private final AuthTokenService authTokenService;
    private final JavaMailSender mailSender;

    public AuthListener(AuthTokenService authTokenService, JavaMailSender mailSender) {
        this.authTokenService = authTokenService;
        this.mailSender = mailSender;
    }

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        this.sendAuthToken(event);
    }

    private void sendAuthToken(final OnRegistrationCompleteEvent event) {
        UserEntity user = event.getUser();
        String token = UUID.randomUUID().toString();
        authTokenService.createToken(user, token);
        SimpleMailMessage email = constructEmailMessage(user, token);
        mailSender.send(email);
    }

    private SimpleMailMessage constructEmailMessage(UserEntity user, String token) {
        String recipientAddress = user.getEmail();
        String subject = "Вход на портал VSE В ПЛЮСЕ";
        String message = """
                Здравствуйте!
                Вы получили это сообщение, так как ваш email адрес был использован при входе на портал VSE В ПЛЮСЕ.
                Срок жизни токена - 60 минут.
                Токен является одноразовым.
                Введите этот токен на странице входа:""";
        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom("no-reply-vse-vplus@mail.ru");
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message + " \r\n" + token);
        return email;
    }
}
