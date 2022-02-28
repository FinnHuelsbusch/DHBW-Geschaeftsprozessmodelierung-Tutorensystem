package com.dhbw.tutorsystem.security.authentication;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class JwtResponse {
    @Getter
    @Setter
    private List<String> roles;

    @Getter
    @Setter
    private String token;

    private static final String type = "Bearer";

    @Getter
    @Setter
    private Integer userId;

    @Getter
    @Setter
    private String email;

    public JwtResponse(String accessToken, Integer userId, String email, List<String> roles) {
        this.token = accessToken;
        this.userId = userId;
        this.email = email;
        this.roles = roles;
    }

}
