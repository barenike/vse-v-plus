package com.example.vse_back.model.service.email_verification;

import com.example.vse_back.model.entity.UserEntity;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {
    private final VerificationTokenService verificationTokenService;
    private final JavaMailSender mailSender;

    public RegistrationListener(VerificationTokenService verificationTokenService, JavaMailSender mailSender) {
        this.verificationTokenService = verificationTokenService;
        this.mailSender = mailSender;
    }

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(final OnRegistrationCompleteEvent event) {
        UserEntity user = event.getUser();
        final String token = UUID.randomUUID().toString();
        verificationTokenService.createToken(user, token);
        SimpleMailMessage email = constructEmailMessage(event, user, token);
        mailSender.send(email);
    }

    private SimpleMailMessage constructEmailMessage(OnRegistrationCompleteEvent event, UserEntity user, String token) {
        String recipientAddress = user.getEmail();
        String subject = "Подтверждение регистрации на портале VSE В ПЛЮСЕ";
        String confirmationUrl = event.getAppUrl() + "/register/confirm?token=" + token;
        String message = "Здравствуйте! Вы получили это сообщение, так как ваш адрес был использован при регистрации нового пользователя в VSE В ПЛЮСЕ. Если вы не не регистрировались на этом портале - проигнорируйте это письмо." +
                "\nДля подтверждения регистрации перейдите по следующей ссылке:";
        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom("no-reply-vse-vplus@mail.ru");
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message + " \r\n" + confirmationUrl);
        return email;
    }
}
