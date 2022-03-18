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

    public static CourseWithTitle convertToDto(ModelMapper modelMapper, Course course) {
        CourseWithTitle courseWithTitleAndLeaders = modelMapper.map(course, CourseWithTitle.class);
        return courseWithTitleAndLeaders;
    }

    public static List<CourseWithTitle> convertToDto(ModelMapper modelMapper, Iterable<Course> courses) {
        ArrayList<CourseWithTitle> coursesList = new ArrayList<>();
        for (Course course : courses) {
            coursesList.add(convertToDto(modelMapper, course));
        }
        return coursesList;
    }
}
