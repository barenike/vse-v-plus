package com.example.vse_back.infrastructure.user;

import lombok.Data;

import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Data
public class RegistrationRequest implements Serializable {
    // In production change to @chelpipegroup.com
    // @Pattern(regexp = "[\\w!#$%&'.*+/=?^`{|}~-]*@mail\\.ru")
    private String email;

    @Pattern(regexp = "(?=.*?[a-z])(?=.*?[A-Z])(?=.*?\\d)\\S{8,255}")
    private String password;

    private String phoneNumber;

    private String firstName;

    private String lastName;
}
