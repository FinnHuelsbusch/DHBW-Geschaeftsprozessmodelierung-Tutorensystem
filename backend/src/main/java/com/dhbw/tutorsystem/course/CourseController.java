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

    @Operation(summary = "Get courses with title and leaders.", tags = { "courses" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Courses with title and leaders are returned as list."),
    })
    @GetMapping("/withTitleAndLeaders")
    // get all Courses with title and leaders by getting all Courses from the DB and converting them before returning 
    public ResponseEntity<List<CourseWithTitleAndLeaders>> getCoursesWithTitleAndLeaders() {
        return new ResponseEntity<List<CourseWithTitleAndLeaders>>(
                CourseWithTitleAndLeaders.convertToDto(modelMapper, courseRepository.findAll()), HttpStatus.OK);
    }

    @Operation(summary = "Get courses with title and specialisations.", tags = { "courses" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Courses with title and specialisations are returned as list."),
    })
    @GetMapping("/withTitleAndSpecialisations")
    // get all Courses with title and specialistationcourse by getting all Courses from the DB and converting before returning 
    public ResponseEntity<List<CourseWithTitleAndSpecialisations>> getCoursesWithTitleAndSpecialisations() {
        return new ResponseEntity<List<CourseWithTitleAndSpecialisations>>(
                CourseWithTitleAndSpecialisations.convertToDto(modelMapper, courseRepository.findAll()), HttpStatus.OK);
    }
}
