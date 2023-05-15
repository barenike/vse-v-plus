package com.example.vse_back.infrastructure.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class BasicUserInfoResponse {
    private UUID id;

    private String email;

    private Integer userBalance;

    private String firstName;

    private String lastName;
}
