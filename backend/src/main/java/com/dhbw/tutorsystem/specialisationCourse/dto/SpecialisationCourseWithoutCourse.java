package com.dhbw.tutorsystem.specialisationCourse.dto;

import java.util.ArrayList;
import java.util.List;

import com.dhbw.tutorsystem.specialisationCourse.SpecialisationCourse;

import org.modelmapper.ModelMapper;

import lombok.Data;

@Data
public class SpecialisationCourseWithoutCourse {

    private Integer id;

    private String title;

    private String abbreviation;

    public static SpecialisationCourseWithoutCourse convertToDto(ModelMapper modelMapper,
            SpecialisationCourse specialisationCourse) {
        SpecialisationCourseWithoutCourse specialisationCourseWithoutCourse = modelMapper.map(specialisationCourse,
                SpecialisationCourseWithoutCourse.class);
        return specialisationCourseWithoutCourse;
    }

    public static List<SpecialisationCourseWithoutCourse> convertToDto(ModelMapper modelMapper,
            Iterable<SpecialisationCourse> specialisationCourses) {
        ArrayList<SpecialisationCourseWithoutCourse> coursesList = new ArrayList<>();
        for (SpecialisationCourse specialisationCourse : specialisationCourses) {
            coursesList.add(convertToDto(modelMapper, specialisationCourse));
        }
        return coursesList;
    }
}
