package com.dhbw.tutorsystem.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
public class UserWithEmailAndName{

    private String firstName;

    private String lastName;

    private String email; 
}
