package com.dhbw.tutorsystem.tutorial.payload;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data       
public class DeleteTutorial {

    private String reason; 
}
