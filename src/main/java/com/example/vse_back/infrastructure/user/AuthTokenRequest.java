package com.example.vse_back.infrastructure.user;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AuthTokenRequest {
    // @Pattern(regexp = "[\\w!#$%&'.*+/=?^`{|}~-]*@chelpipegroup\\.com")
    private String email;

    @NotNull
    private String token;
}
