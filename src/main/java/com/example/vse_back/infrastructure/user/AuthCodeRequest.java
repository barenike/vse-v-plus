package com.example.vse_back.infrastructure.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AuthCodeRequest {
    // In prod change to @Pattern(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@chelpipegroup\\.com$")
    private String email;

    @NotNull
    // Do I need this length constraint in other places too?
    @Pattern(regexp = "^\\d{6}$")
    private String code;
}
