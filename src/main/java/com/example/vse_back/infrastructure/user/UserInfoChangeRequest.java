package com.example.vse_back.infrastructure.user;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserInfoChangeRequest {
    private Integer userBalance;

    private String phoneNumber;

    private String firstName;

    private String lastName;

    private String jobTitle;

    private String infoAbout;

    private MultipartFile file;
}
