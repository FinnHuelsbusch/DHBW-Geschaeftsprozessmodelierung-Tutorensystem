package com.dhbw.tutorsystem.security.authentication;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

public class ResetPasswordRequest {

    @NotBlank
    @Getter
    @Setter
    private String hash;

    @NotBlank
    @Getter
    @Setter
    private String email;

    @NotBlank
    @Getter
    @Setter
    private String password;

}