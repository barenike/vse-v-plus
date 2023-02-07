package com.example.vse_back.infrastructure.user;

import lombok.Data;

import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Data
public class ResetPasswordRequest implements Serializable {
    @Pattern(regexp = "(?=.*?[a-z])(?=.*?[A-Z])(?=.*?\\d)\\S{8,255}")
    private String password;
}
