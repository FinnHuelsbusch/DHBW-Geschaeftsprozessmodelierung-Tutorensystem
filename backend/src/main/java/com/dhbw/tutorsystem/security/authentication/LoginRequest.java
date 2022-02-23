package com.dhbw.tutorsystem.security.authentication;

import javax.validation.constraints.NotBlank;

public class LoginRequest {
    @NotBlank
    private String emailAddress;

    @NotBlank
    private String password;

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
