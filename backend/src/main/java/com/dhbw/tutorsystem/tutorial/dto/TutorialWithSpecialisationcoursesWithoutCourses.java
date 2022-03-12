package com.dhbw.tutorsystem.tutorial.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.dhbw.tutorsystem.specialisationCourse.SpecialisationCourse;
import com.dhbw.tutorsystem.specialisationCourse.dto.SpecialisationCourseWithoutCourse;
import com.dhbw.tutorsystem.tutorial.Tutorial;
import com.dhbw.tutorsystem.user.User;
import com.dhbw.tutorsystem.user.student.Student;

import org.modelmapper.ModelMapper;

import lombok.Data;

@Data
public class TutorialWithSpecialisationcoursesWithoutCourses {

    private Integer id;

    private String appointment;

    private String description;

    private String title;

    private int durationMinutes;

    private LocalDate start;

    private LocalDate end;

    private Set<User> tutors;

    private Set<Student> participants;

    private Set<SpecialisationCourseWithoutCourse> specialisationCourses;

    public static TutorialWithSpecialisationcoursesWithoutCourses convertToDto(ModelMapper modelMapper, Tutorial tutorial){
        TutorialWithSpecialisationcoursesWithoutCourses tutorialWithSpecialisationcoursesWithoutCourses = modelMapper.map(tutorial, TutorialWithSpecialisationcoursesWithoutCourses.class); 
        return tutorialWithSpecialisationcoursesWithoutCourses;
    }

    public static List<TutorialWithSpecialisationcoursesWithoutCourses> convertToDto(ModelMapper modelMapper, Iterable<Tutorial> tutorials){
        ArrayList<TutorialWithSpecialisationcoursesWithoutCourses> tutorialWithSpecialisationcoursesWithoutCoursesList = new ArrayList<>(); 
        for (Tutorial tutorial : tutorials) {
            tutorialWithSpecialisationcoursesWithoutCoursesList.add(convertToDto(modelMapper, tutorial)); 
        }
        return tutorialWithSpecialisationcoursesWithoutCoursesList;
    }
}
