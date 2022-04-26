package com.dhbw.tutorsystem.security.authentication.payload;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.dhbw.tutorsystem.user.User;

import com.dhbw.tutorsystem.security.authentication.annotation.ValidEmail;
import com.dhbw.tutorsystem.security.authentication.annotation.ValidPassword;

import lombok.Getter;
import lombok.Setter;

public class ResetPasswordRequest {

    @NotBlank
    @Getter
    @Setter
    private String hash;

    @ValidEmail
    @Getter
    @Setter
    private String email;

    @ValidPassword
    @Getter
    @Setter
    private String newPassword;

}