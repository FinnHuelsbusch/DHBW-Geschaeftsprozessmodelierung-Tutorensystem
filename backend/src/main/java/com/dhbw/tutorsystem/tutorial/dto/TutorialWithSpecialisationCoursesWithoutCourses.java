package com.dhbw.tutorsystem.tutorial.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.dhbw.tutorsystem.specialisationCourse.SpecialisationCourse;
import com.dhbw.tutorsystem.specialisationCourse.dto.SpecialisationCourseWithoutCourse;
import com.dhbw.tutorsystem.tutorial.Tutorial;
import com.dhbw.tutorsystem.user.User;
import com.dhbw.tutorsystem.user.dto.UserWithEmailAndNameAndId;
import com.dhbw.tutorsystem.user.student.Student;

import org.modelmapper.ModelMapper;

import lombok.Data;

@Data
public class TutorialWithSpecialisationCoursesWithoutCourses {

    private Integer id;

    private String appointment;

    private String description;

    private String title;

    private int durationMinutes;

    private LocalDate start;

    private LocalDate end;

    private Set<UserWithEmailAndNameAndId> tutors;

    private Set<UserWithEmailAndNameAndId> participants;

    private Set<SpecialisationCourseWithoutCourse> specialisationCourses;

    public static TutorialWithSpecialisationCoursesWithoutCourses convertToDto(ModelMapper modelMapper, Tutorial tutorial){
        TutorialWithSpecialisationCoursesWithoutCourses tutorialWithSpecialisationcoursesWithoutCourses = modelMapper.map(tutorial, TutorialWithSpecialisationCoursesWithoutCourses.class); 
        return tutorialWithSpecialisationcoursesWithoutCourses;
    }

    public static List<TutorialWithSpecialisationCoursesWithoutCourses> convertToDto(ModelMapper modelMapper, Iterable<Tutorial> tutorials){
        ArrayList<TutorialWithSpecialisationCoursesWithoutCourses> tutorialWithSpecialisationcoursesWithoutCoursesList = new ArrayList<>(); 
        for (Tutorial tutorial : tutorials) {
            tutorialWithSpecialisationcoursesWithoutCoursesList.add(convertToDto(modelMapper, tutorial)); 
        }
        return tutorialWithSpecialisationcoursesWithoutCoursesList;
    }
}
