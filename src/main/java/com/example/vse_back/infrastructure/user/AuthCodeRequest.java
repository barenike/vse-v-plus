package com.example.vse_back.infrastructure.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AuthCodeRequest {
    // @Pattern(regexp = "[\\w!#$%&'.*+/=?^`{|}~-]*@chelpipegroup\\.com$")
    private String email;

    @NotNull
    @Pattern(regexp = "^\\d{6}$")
    private String code;
}
