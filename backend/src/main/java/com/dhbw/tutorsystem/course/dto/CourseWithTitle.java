package com.dhbw.tutorsystem.course.dto;

import java.util.ArrayList;
import java.util.List;

import com.dhbw.tutorsystem.course.Course;

import org.modelmapper.ModelMapper;

import lombok.Data;

@Data
public class CourseWithTitle {

    private Integer id;

    private String title;

    private String abbreviation;

    // maps a given course to a course with title and abbreviation
    public static CourseWithTitle convertToDto(ModelMapper modelMapper, Course course) {
        CourseWithTitle courseWithTitleAndLeaders = modelMapper.map(course, CourseWithTitle.class);
        return courseWithTitleAndLeaders;
    }

    // maps multiple given courses to courses with title and abbreviation
    public static List<CourseWithTitle> convertToDto(ModelMapper modelMapper, Iterable<Course> courses) {
        ArrayList<CourseWithTitle> coursesList = new ArrayList<>();
        for (Course course : courses) {
            coursesList.add(convertToDto(modelMapper, course));
        }
        return coursesList;
    }
}
