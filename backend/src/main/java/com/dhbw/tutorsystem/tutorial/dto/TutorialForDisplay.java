package com.dhbw.tutorsystem.tutorial.dto;

import java.time.LocalDate;
import java.util.Set;

import com.dhbw.tutorsystem.specialisationCourse.dto.SpecialisationCourseWithCourse;
import com.dhbw.tutorsystem.tutorial.Tutorial;
import com.dhbw.tutorsystem.user.dto.UserWithEmailAndNameAndId;

import org.modelmapper.ModelMapper;

import lombok.Data;

@Data
public class TutorialForDisplay {

    private Integer id;

    private String title;

    private String appointment;

    private String description;

    private int durationMinutes;

    private LocalDate start;

    private LocalDate end;

    private Set<UserWithEmailAndNameAndId> tutors;

    private int numberOfParticipants;

    private Set<SpecialisationCourseWithCourse> specialisationCourses;

    private boolean isMarked;

    private boolean participates;

    private boolean holds;

    public TutorialForDisplay addPerspective(boolean isMarked, boolean participates, boolean holds) {
        this.isMarked = isMarked;
        this.participates = participates;
        this.holds = holds;
        return this;
    }

    public static TutorialForDisplay convertToDto(ModelMapper modelMapper, Tutorial tutorial) {
        TutorialForDisplay tutorialDto = modelMapper.map(tutorial, TutorialForDisplay.class);
        int numberOfParticipants = tutorial.getParticipants() != null ? tutorial.getParticipants().size() : 0;
        tutorialDto.setNumberOfParticipants(numberOfParticipants);
        return tutorialDto;
    }

}
