package com.dhbw.tutorsystem.course;

import java.util.List;


import com.dhbw.tutorsystem.course.dto.CourseWithTitleAndLeaders;
import com.dhbw.tutorsystem.exception.TSExceptionResponse;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
// import io.swagger.v3.oas.annotations.responses.ApiResponses;
// import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;


@RestController
@RequestMapping("/courses")
@AllArgsConstructor
public class CourseController {

    private final ModelMapper modelMapper; 
    private final CourseRepository courseRepository; 
    

    @Operation(summary = "Get Courses without Specialisation", tags = { "authentication" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = " Courses without Specialisation are returned as list."),
    })
    @GetMapping("/withoutSpecialisation")
    public ResponseEntity<List<CourseWithTitleAndLeaders>> getCourses() {
        return new ResponseEntity<List<CourseWithTitleAndLeaders>>(CourseWithTitleAndLeaders.convertToDto(modelMapper, courseRepository.findAll()), HttpStatus.OK);
    }
}
