package com.dhbw.tutorsystem.tutorialOffer;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class CreateTutorialOfferRequest {

    @Getter
    @Setter
    @NotNull
    private LocalDate start;

    @Getter
    @Setter
    @NotNull
    private LocalDate end;

    @Getter
    @Setter
    @NotNull
    private String description;
}

