package com.dhbw.tutorsystem.security.authentication;

public class SignupResponse {

    private String emailAddress;

    public SignupResponse(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}

