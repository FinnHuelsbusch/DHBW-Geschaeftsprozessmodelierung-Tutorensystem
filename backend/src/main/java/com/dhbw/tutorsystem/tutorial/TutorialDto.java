package com.dhbw.tutorsystem.tutorial;

import java.time.LocalDate;
import java.util.Set;

import com.dhbw.tutorsystem.specialisationCourse.SpecialisationCourse;

import lombok.Data;

@Data
public class TutorialDto {

    private Integer id;

    private String title;

    private String appointment;

    private String description;

    private int durationMinutes;

    private LocalDate start;

    private LocalDate end;

    private Set<UserDto> tutors;

    private int numberOfParticipants;

    private Set<SpecialisationCourse> specialisationCourses;

}
