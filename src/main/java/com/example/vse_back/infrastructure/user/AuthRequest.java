package com.example.vse_back.infrastructure.user;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class AuthRequest implements Serializable {
    @NotNull
    private String email;

    @NotNull
    private String password;
}
