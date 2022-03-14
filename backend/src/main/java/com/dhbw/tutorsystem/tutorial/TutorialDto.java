package com.dhbw.tutorsystem.tutorial;

import java.time.LocalDate;
import java.util.Set;

import com.dhbw.tutorsystem.course.dto.CourseWithTitleAndSpecialisations;
import com.dhbw.tutorsystem.specialisationCourse.SpecialisationCourse;
import com.dhbw.tutorsystem.specialisationCourse.dto.SpecialisationCourseWithCourse;
import com.dhbw.tutorsystem.specialisationCourse.dto.SpecialisationCourseWithoutCourse;
import com.dhbw.tutorsystem.user.dto.UserWithEmailAndNameAndId;

import org.modelmapper.ModelMapper;

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

    private Set<UserWithEmailAndNameAndId> tutors;

    private int numberOfParticipants;

    private Set<SpecialisationCourseWithCourse> specialisationCourses;

    private boolean isMarked;

    private boolean participates;

    public TutorialDto addPerspective(boolean isMarked, boolean participates) {
        this.isMarked = isMarked;
        this.participates = participates;
        return this;
    }

    public static TutorialDto convertToDto(ModelMapper modelMapper, Tutorial tutorial) {
        TutorialDto tutorialDto = modelMapper.map(tutorial, TutorialDto.class);
        // tutorialDto.setTutors(tutorial.getTutors().stream().map(tutor ->
        // UserWithEmailAndNameAndId.convertToDto(tutor))
        // .collect(Collectors.toSet()));
        int numberOfParticipants = tutorial.getParticipants() != null ? tutorial.getParticipants().size() : 0;
        tutorialDto.setNumberOfParticipants(numberOfParticipants);
        return tutorialDto;
    }

}
