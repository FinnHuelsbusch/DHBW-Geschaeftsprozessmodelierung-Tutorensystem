package com.dhbw.tutorsystem.user;


import java.util.List;

import com.dhbw.tutorsystem.user.dto.UserWithEmailAndNameAndId;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
@SecurityScheme(name = "jwt-auth", type = SecuritySchemeType.HTTP, scheme = "bearer")
public class UserController {


    private final ModelMapper modelMapper; 
    private final UserRepository userRepository; 

    @Operation(summary = "Get Users", tags = { "users" }, description = "Get all Users except Admin users", security = @SecurityRequirement(name = "jwt-auth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All users are returned."),
    })
    @GetMapping("")
    @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_DIRECTOR')")
    public ResponseEntity<List<UserWithEmailAndNameAndId>> getAllUsers() {
        List<User> users = userRepository.findAllUsersThatAreNotAdmin();
        return new ResponseEntity<List<UserWithEmailAndNameAndId>>(UserWithEmailAndNameAndId.convertToDto(modelMapper, users), HttpStatus.OK);
    }
}
