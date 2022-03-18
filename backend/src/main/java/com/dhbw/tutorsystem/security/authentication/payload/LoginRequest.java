package com.dhbw.tutorsystem.security.authentication.payload;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

import com.dhbw.tutorsystem.security.authentication.annotation.ValidEmail;

public class LoginRequest {

    @ValidEmail
    @Getter
    @Setter
    private String email;

    @NotBlank
    @Getter
    @Setter
    // intentionally do not check password regex here to not expose details
    private String password;

}
