package com.dhbw.tutorsystem.security.authentication.payload;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class JwtResponse {
    @NotBlank
    @Getter
    @Setter
    private List<String> roles;

    @NotBlank
    @Getter
    @Setter
    private String token;

    @NotBlank
    @Getter
    @Setter
    private Date expirationDate;

    @NotBlank
    @Getter
    @Setter
    private String email;

}
