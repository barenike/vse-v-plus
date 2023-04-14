package com.example.vse_back.model.service.email_verification;

import com.example.vse_back.model.entity.UserEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Setter
@Getter
public class OnRegistrationCompleteEvent extends ApplicationEvent {
    private UserEntity user;

    public OnRegistrationCompleteEvent(UserEntity user) {
        super(user);
        this.user = user;
    }
}
