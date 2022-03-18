package com.dhbw.tutorsystem.tutorialRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class CreateTutorialRequestRequest {
    @Getter
    @Setter
    @NotBlank
    private String title;

    @Getter
    @Setter
    @NotBlank
    private String description;

    @Getter
    @Setter
    @NotNull
    private int semester;
}
