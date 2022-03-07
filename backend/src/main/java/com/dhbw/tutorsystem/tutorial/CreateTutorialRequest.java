package com.dhbw.tutorsystem.tutorial;

import java.time.LocalDate;
import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.dhbw.tutorsystem.specialisationCourse.SpecialisationCourse;
import com.dhbw.tutorsystem.user.User;

import lombok.Getter;
import lombok.Setter;

public class CreateTutorialRequest {
    
    @Getter
    @Setter
    @NotBlank
    private String titel; 

    @Getter
    @Setter
    @NotBlank
    private String description; 

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
    private int durationMinutes; 
    
    @Getter
    @Setter
    private Set<User> tutors; 

    @Getter
    @Setter
    private Set<SpecialisationCourse> spcialisationCourses;
}
