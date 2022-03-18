package com.dhbw.tutorsystem.course;

import java.util.List;

import com.dhbw.tutorsystem.course.dto.CourseWithTitleAndLeaders;
import com.dhbw.tutorsystem.course.dto.CourseWithTitleAndSpecialisations;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/courses")
@AllArgsConstructor
public class CourseController {

    private final ModelMapper modelMapper;
    private final CourseRepository courseRepository;

    @Operation(summary = "Get Courses with Title and Leaders", tags = { "courses" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = " Courses with Title and Leaders are returned as list."),
    })
    @GetMapping("/withTitleAndLeaders")
    public ResponseEntity<List<CourseWithTitleAndLeaders>> getCoursesWithTitleAndLeaders() {
        return new ResponseEntity<List<CourseWithTitleAndLeaders>>(
                CourseWithTitleAndLeaders.convertToDto(modelMapper, courseRepository.findAll()), HttpStatus.OK);
    }

    @Operation(summary = "Get Courses with Title and Specialisations", tags = { "courses" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Courses with Title and Specialisations are returned as list."),
    })
    @GetMapping("/withTitleAndSpecialisations")
    public ResponseEntity<List<CourseWithTitleAndSpecialisations>> getCoursesWithTitleAndSpecialisations() {
        return new ResponseEntity<List<CourseWithTitleAndSpecialisations>>(
                CourseWithTitleAndSpecialisations.convertToDto(modelMapper, courseRepository.findAll()), HttpStatus.OK);
    }
}
