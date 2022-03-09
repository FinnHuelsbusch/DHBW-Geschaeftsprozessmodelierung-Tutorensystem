package com.dhbw.tutorsystem.security.authentication.payload;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

public class LoginRequest {
    @NotBlank
    @Getter
    @Setter
    private String email;

    @NotBlank
    @Getter
    @Setter
    private String password;

}
