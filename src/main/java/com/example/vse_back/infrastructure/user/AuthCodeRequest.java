package com.example.vse_back.infrastructure.user;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class AuthCodeRequest {
    // @Pattern(regexp = "[\\w!#$%&'.*+/=?^`{|}~-]*@chelpipegroup\\.com$")
    private String email;

    @NotNull
    @Pattern(regexp = "^\\d{6}$")
    private String code;
}
