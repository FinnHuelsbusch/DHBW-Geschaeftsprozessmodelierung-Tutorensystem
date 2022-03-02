package com.dhbw.tutorsystem.security.authentication;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

public class RequestPasswordResetRequest {

    @NotBlank
    @Getter
    @Setter
    private String email;

}