package com.dhbw.tutorsystem.security.authentication;

import java.util.List;

public class JwtResponse {
    private final List<String> roles;
    private String token;
    private String type = "Bearer";
    private Integer userId;
    private String email;

    public JwtResponse(String accessToken, Integer userId, String email, List<String> roles) {
        this.token = accessToken;
        this.userId = userId;
        this.email = email;
        this.roles = roles;
    }

    public String getAccessToken() {
        return token;
    }

    public void setAccessToken(String accessToken) {
        this.token = accessToken;
    }

    public String getTokenType() {
        return type;
    }

    public void setTokenType(String tokenType) {
        this.type = tokenType;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getRoles() {
        return roles;
    }
}

