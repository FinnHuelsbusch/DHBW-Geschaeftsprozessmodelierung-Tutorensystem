
package com.dhbw.tutorsystem.tutorial.payload;

import java.time.LocalDate;
import java.util.Set;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

public class TutorialDataRequest {

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
    @NotBlank
    private String appointment;

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
    @Min(1)
    private int durationMinutes;

    @Getter
    @Setter
    @NotEmpty
    private Set<String> tutorEmails;

    @Getter
    @Setter
    @NotEmpty
    private Set<Integer> specialisationCoursesIds;
}
