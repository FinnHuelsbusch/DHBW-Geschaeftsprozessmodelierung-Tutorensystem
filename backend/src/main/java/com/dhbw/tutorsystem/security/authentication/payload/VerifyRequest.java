package com.dhbw.tutorsystem.security.authentication.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
public class VerifyRequest {

    @NotBlank
    @Getter
    @Setter
    private String hash;

    @NotBlank
    @Getter
    @Setter
    private String email;

}
