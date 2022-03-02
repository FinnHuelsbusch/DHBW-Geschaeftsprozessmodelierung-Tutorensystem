package com.dhbw.tutorsystem.security.authentication.payload;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class JwtResponse {
    @Getter
    @Setter
    private List<String> roles;

    @Getter
    @Setter
    private String token;

    @Getter
    @Setter
    private Date expirationDate;

    @Getter
    @Setter
    private String email;

}
