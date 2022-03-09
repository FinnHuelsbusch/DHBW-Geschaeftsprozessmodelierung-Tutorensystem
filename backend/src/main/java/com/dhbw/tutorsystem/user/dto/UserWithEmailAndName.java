package com.dhbw.tutorsystem.user.dto;

import lombok.Data;

@Data
public class UserWithEmailAndName{

    private String firstName;

    private String lastName;

    private String email; 
}
