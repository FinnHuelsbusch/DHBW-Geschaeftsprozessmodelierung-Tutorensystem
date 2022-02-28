package com.dhbw.tutorsystem.security.authentication;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class SignupRequest {

    @NotBlank
    @Getter
    @Setter
    private String email;

    @NotBlank
    @Size(min = 6, max = 40)
    @Getter
    @Setter
    private String password;

}
