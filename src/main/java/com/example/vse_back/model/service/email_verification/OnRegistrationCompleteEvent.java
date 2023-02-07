package com.example.vse_back.model.service.email_verification;

import com.example.vse_back.model.entity.UserEntity;
import org.springframework.context.ApplicationEvent;

public class OnRegistrationCompleteEvent extends ApplicationEvent {
    private UserEntity user;
    private String appUrl;

    public OnRegistrationCompleteEvent(UserEntity user, String appUrl) {
        super(user);
        this.user = user;
        this.appUrl = appUrl;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getAppUrl() {
        return appUrl;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }
}
