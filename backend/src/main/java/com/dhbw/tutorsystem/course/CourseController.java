package com.dhbw.tutorsystem.course;

import java.util.List;

import com.dhbw.tutorsystem.exception.TSExceptionResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
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

    private final CourseRepository courseRepository; 
    

    @Operation(summary = "Login a user based on email and password.", tags = { "authentication" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login was successful. User is logged by using the token in the response."),
            @ApiResponse(responseCode = "400", description = "Login was not successful.", content = @Content(schema = @Schema(implementation = TSExceptionResponse.class)))
    })
    @GetMapping()
    public ResponseEntity<Iterable<Course>> getCourses() {
        return new ResponseEntity<Iterable<Course>>(courseRepository.findAll(), HttpStatus.OK);
    }
}