package com.example.vse_back.infrastructure.user;

import lombok.Data;

@Data
public class UserInfoChangeRequest {
    private String phoneNumber;

    private String firstName;

    private String lastName;

    private String jobTitle;

    private String infoAbout;
}
