package com.example.vse_back.infrastructure.user;

import com.example.vse_back.model.entity.ImageEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FullUserInfoResponse {
    private String id;

    private String roleId;

    private String email;

    private String userBalance;

    private String phoneNumber;

    private String firstName;

    private String lastName;

    private String jobTitle;

    private String infoAbout;

    private ImageEntity image;
}
