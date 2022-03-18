package com.dhbw.tutorsystem.security.authentication.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

import com.dhbw.tutorsystem.security.authentication.annotation.ValidEmail;

@AllArgsConstructor
public class VerifyRequest {

    @NotBlank
    @Getter
    @Setter
    private String hash;

    @ValidEmail
    @Getter
    @Setter
    private String email;

}
