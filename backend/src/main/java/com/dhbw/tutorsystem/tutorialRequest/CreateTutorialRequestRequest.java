package com.dhbw.tutorsystem.tutorialRequest;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class CreateTutorialRequestRequest {
    @Getter
    @Setter
    @NotNull
    private String description;
}
