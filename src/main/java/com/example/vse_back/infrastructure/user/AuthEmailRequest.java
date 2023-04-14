package com.example.vse_back.infrastructure.user;

import lombok.Data;

@Data
public class AuthEmailRequest {
    // @Pattern(regexp = "[\\w!#$%&'.*+/=?^`{|}~-]*@chelpipegroup\\.com$")
    private String email;
}
