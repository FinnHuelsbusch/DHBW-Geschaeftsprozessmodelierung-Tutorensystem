package com.dhbw.tutorsystem.user;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.dhbw.tutorsystem.exception.TSExceptionResponse;
import com.dhbw.tutorsystem.role.ERole;
import com.dhbw.tutorsystem.security.authentication.exception.UserNotFoundException;
import com.dhbw.tutorsystem.specialisationCourse.SpecialisationCourse;
import com.dhbw.tutorsystem.specialisationCourse.SpecialisationCourseRepository;
import com.dhbw.tutorsystem.tutorial.exception.SpecialisationCourseNotFoundException;
import com.dhbw.tutorsystem.user.director.Director;
import com.dhbw.tutorsystem.user.director.DirectorRepository;
import com.dhbw.tutorsystem.user.dto.UserWithEmailAndNameAndId;
import com.dhbw.tutorsystem.user.student.Student;
import com.dhbw.tutorsystem.user.student.StudentRepository;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
        private final StudentRepository studentRepository;
        private final DirectorRepository directorRepository;
        private final SpecialisationCourseRepository specialisationCourseRepository;

        @Operation(summary = "Get Users", tags = {
                        "users" }, description = "Get all Users except Admin users", security = @SecurityRequirement(name = "jwt-auth"))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "All users are returned."),
        })
        @GetMapping("")
        @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_DIRECTOR')")
        // get all users excluding admins
        public ResponseEntity<List<UserWithEmailAndNameAndId>> getAllUsers() {
                List<User> users = userRepository.findAllUsersThatAreNotAdmin();
                return new ResponseEntity<List<UserWithEmailAndNameAndId>>(
                                UserWithEmailAndNameAndId.convertToDto(modelMapper, users), HttpStatus.OK);
        }

        @Operation(tags = {
                        "user" }, summary = "Update user information.", description = "Update firstname, lastname and specialisationcourse (only for students) and get it as return.", security = @SecurityRequirement(name = "jwt-auth"))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Returns the updated user."),
                        @ApiResponse(responseCode = "400", description = "One of the parameters was not set correctly.", content = @Content(schema = @Schema(implementation = TSExceptionResponse.class)))
        })
        @PostMapping("/update")
        @PreAuthorize("hasAnyRole('ROLE_DIRECTOR','ROLE_STUDENT','ROLE_ADMIN')")
        public ResponseEntity<UserWithEmailAndNameAndId> updateUser(
                        @RequestBody @NotNull @Valid UpdateUserRequest updateUserRequest) {

                User user = null;

                // check, if user exists in database. In case the user exists, the firstname,
                // lastname and specialisationcourse (only students) will be updated
                if (userRepository.existsByEmail(updateUserRequest.getEmail())) {
                        // check if user is student
                        if (userRepository.findByEmail(updateUserRequest.getEmail()).get().getRoles().iterator().next()
                                        .getName()
                                        .equals(ERole.ROLE_STUDENT)) {

                                Student student = studentRepository.findByEmail(updateUserRequest.getEmail())
                                                .orElseThrow(UserNotFoundException::new);

                                Optional<SpecialisationCourse> specialisationCourse = specialisationCourseRepository
                                                .findById(updateUserRequest.getSpecialisationCourseId());
                                if (specialisationCourse.isEmpty()) {
                                        throw new SpecialisationCourseNotFoundException();
                                }

                                if (updateUserRequest.getFirstName() != null && updateUserRequest.getLastName() != null
                                                && updateUserRequest.getSpecialisationCourseId() != null) {
                                        // setting the new values
                                        student.setFirstName(updateUserRequest.getFirstName());
                                        student.setLastName(updateUserRequest.getLastName());
                                        student.setSpecialisationCourse(specialisationCourse.get());
                                        user = student;
                                        userRepository.save(user);
                                }

                                // check if user is director
                        } else if (userRepository.findByEmail(updateUserRequest.getEmail()).get().getRoles().iterator()
                                        .next().getName()
                                        .equals(ERole.ROLE_DIRECTOR)) {
                                Director director = directorRepository.findByEmail(updateUserRequest.getEmail())
                                                .orElseThrow(UserNotFoundException::new);

                                if (updateUserRequest.getFirstName() != null
                                                && updateUserRequest.getLastName() != null) {
                                        // setting the new values
                                        director.setFirstName(updateUserRequest.getFirstName());
                                        director.setLastName(updateUserRequest.getLastName());
                                        user = director;
                                        userRepository.save(user);
                                }

                                // check if user is admin
                        } else if (userRepository.findByEmail(updateUserRequest.getEmail()).get().getRoles().iterator()
                                        .next().getName()
                                        .equals(ERole.ROLE_ADMIN)) {
                                User admin = userRepository.findByEmail(updateUserRequest.getEmail())
                                                .orElseThrow(UserNotFoundException::new);

                                if (updateUserRequest.getFirstName() != null
                                                && updateUserRequest.getLastName() != null) {
                                        // setting the new values
                                        admin.setFirstName(updateUserRequest.getFirstName());
                                        admin.setLastName(updateUserRequest.getLastName());
                                        user = admin;
                                        userRepository.save(user);
                                }
                        }
                }

                return new ResponseEntity<UserWithEmailAndNameAndId>(
                                UserWithEmailAndNameAndId.convertToDto(modelMapper, user), HttpStatus.OK);
        }
}
