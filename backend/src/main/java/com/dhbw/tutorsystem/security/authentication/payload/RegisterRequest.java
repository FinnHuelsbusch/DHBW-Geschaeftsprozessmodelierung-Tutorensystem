package com.dhbw.tutorsystem.security.authentication.payload;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.dhbw.tutorsystem.user.User;

public class RegisterRequest {

    @NotBlank
    @Getter
    private String email;

    public void setEmail(String email) {
        this.email = email.trim();
    }

    @NotBlank
    @Pattern(regexp = User.passwordRegex)
    @Getter
    @Setter
    private String password;

    @NotBlank
    @Getter
    @Setter
    private String firstName;

    @NotBlank
    @Getter
    @Setter
    private String lastName;

    @Getter
    @Setter
    private Integer specialisationCourseId;

}
