package com.example.vse_back.model.service.email_verification;

import com.example.vse_back.model.entity.UserEntity;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class AuthListener implements ApplicationListener<OnRegistrationCompleteEvent> {
    private final AuthCodeService authCodeService;
    private final JavaMailSender mailSender;

    public AuthListener(AuthCodeService authCodeService, JavaMailSender mailSender) {
        this.authCodeService = authCodeService;
        this.mailSender = mailSender;
    }

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        this.sendAuthCode(event);
    }

    private void sendAuthCode(final OnRegistrationCompleteEvent event) {
        UserEntity user = event.getUser();
        String code = RandomStringUtils.random(6, false, true);
        SecureRandom random = new SecureRandom();
        RandomStringUtils.random(6, 0, 0, false, true, null, random);
        authCodeService.createCode(user, code);
        SimpleMailMessage email = constructEmailMessage(user, code);
        mailSender.send(email);
    }

    private SimpleMailMessage constructEmailMessage(UserEntity user, String code) {
        String recipientAddress = user.getEmail();
        String subject = code + " — VSE В ПЛЮСЕ";
        String message = """
                Здравствуйте!
                                
                Ваш адрес электронной почты был указан для входа на портал VSE В ПЛЮСЕ.
                                
                Пожалуйста, введите этот код на странице авторизации:
                                
                """ + code + """
                                
                                
                Срок жизни кода — 5 минут.
                                
                Код является одноразовым.
                                
                Если это не вы или вы не регистрировались на сайте, то просто проигнорируйте это письмо.
                                
                С наилучшими пожеланиями,
                Четверка Жезлов
                """;
        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom("no-reply-vse-vplus@mail.ru");
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message);
        return email;
    }
}
