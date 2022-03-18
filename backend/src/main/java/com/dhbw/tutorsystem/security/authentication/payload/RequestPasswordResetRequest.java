package com.dhbw.tutorsystem.security.authentication.payload;

import com.dhbw.tutorsystem.security.authentication.annotation.ValidEmail;
import com.dhbw.tutorsystem.security.authentication.annotation.ValidPassword;

import lombok.Getter;
import lombok.Setter;

public class RequestPasswordResetRequest {

    @ValidEmail
    @Getter
    @Setter
    private String email;

    @ValidPassword
    @Getter
    @Setter
    private String newPassword;

}