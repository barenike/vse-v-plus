package com.example.vse_back.infrastructure.user;

import lombok.Data;

@Data
public class AuthEmailRequest {
    // In prod change to @Pattern(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@chelpipegroup\\.com$")
    private String email;
}
