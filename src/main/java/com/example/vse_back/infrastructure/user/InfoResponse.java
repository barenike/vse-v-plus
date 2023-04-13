package com.example.vse_back.infrastructure.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InfoResponse {
    private String uuid;

    private Integer roleId;

    private String email;

    private Integer userBalance;

    private String phoneNumber;

    private String firstName;

    private String secondName;

    private String jobTitle;

    private String infoAbout;
}
