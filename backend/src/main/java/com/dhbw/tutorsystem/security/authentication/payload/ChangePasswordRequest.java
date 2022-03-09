package com.dhbw.tutorsystem.security.authentication.payload;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

public class ChangePasswordRequest {

    @NotBlank
    @Getter
    @Setter
    private String newPassword;

}