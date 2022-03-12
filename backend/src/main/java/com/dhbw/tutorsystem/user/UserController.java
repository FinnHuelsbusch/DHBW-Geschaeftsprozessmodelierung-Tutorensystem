package com.dhbw.tutorsystem.user;


import java.util.List;

import com.dhbw.tutorsystem.course.dto.CourseWithTitleAndLeaders;
import com.dhbw.tutorsystem.user.dto.UserWithEmailAndName;
import com.dhbw.tutorsystem.user.dto.UserWithEmailAndNameAndId;

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
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {


    private final ModelMapper modelMapper; 
    private final UserRepository userRepository; 

    @Operation(summary = "Get Courses with Title and Leaders", tags = { "authentication" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = " Courses with Title and Leaders are returned as list."),
    })
    @GetMapping("")

    //TODO: Exclude Admins
    public ResponseEntity<List<UserWithEmailAndNameAndId>> getAllUsers() {
        return new ResponseEntity<List<UserWithEmailAndNameAndId>>(UserWithEmailAndNameAndId.convertToDto(modelMapper, userRepository.findAll()), HttpStatus.OK);
    }
}
