package com.example.vse_back.infrastructure.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BasicUserInfoResponse {
    private String id;

    private String email;

    private Integer userBalance;

    private String firstName;

    private String lastName;
}
