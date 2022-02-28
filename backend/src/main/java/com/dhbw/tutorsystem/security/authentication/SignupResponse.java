package com.dhbw.tutorsystem.security.authentication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class SignupResponse {

    @Getter
    @Setter
    private String emailAddress;

    @Getter
    @Setter
    private String accessToken;
}

