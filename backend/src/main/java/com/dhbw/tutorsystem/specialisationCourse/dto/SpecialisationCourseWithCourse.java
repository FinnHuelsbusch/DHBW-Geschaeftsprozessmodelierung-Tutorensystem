package com.dhbw.tutorsystem.specialisationCourse.dto;

import java.util.ArrayList;
import java.util.List;

import com.dhbw.tutorsystem.course.dto.CourseWithTitle;
import com.dhbw.tutorsystem.specialisationCourse.SpecialisationCourse;

import org.modelmapper.ModelMapper;

import lombok.Data;

@Data
public class SpecialisationCourseWithCourse {

    private Integer id;

    private String title;

    private String abbreviation;

    private CourseWithTitle course;

    public static SpecialisationCourseWithCourse convertToDto(ModelMapper modelMapper,
            SpecialisationCourse specialisationCourse) {
        SpecialisationCourseWithCourse specialisationCourseWithoutCourse = modelMapper.map(specialisationCourse,
                SpecialisationCourseWithCourse.class);
        return specialisationCourseWithoutCourse;
    }

    public static List<SpecialisationCourseWithCourse> convertToDto(ModelMapper modelMapper,
            Iterable<SpecialisationCourse> specialisationCourses) {
        ArrayList<SpecialisationCourseWithCourse> coursesList = new ArrayList<>();
        for (SpecialisationCourse specialisationCourse : specialisationCourses) {
            coursesList.add(convertToDto(modelMapper, specialisationCourse));
        }
        return coursesList;
    }
}
