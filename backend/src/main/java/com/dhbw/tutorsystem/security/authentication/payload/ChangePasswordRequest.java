package com.dhbw.tutorsystem.security.authentication.payload;

import com.dhbw.tutorsystem.security.authentication.annotation.ValidPassword;

import lombok.Getter;
import lombok.Setter;

public class ChangePasswordRequest {

    @ValidPassword
    @Getter
    @Setter
    private String newPassword;

}