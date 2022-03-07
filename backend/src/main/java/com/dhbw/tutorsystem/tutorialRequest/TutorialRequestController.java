package com.dhbw.tutorsystem.tutorialRequest;

import com.dhbw.tutorsystem.user.student.Student;
import com.dhbw.tutorsystem.user.student.StudentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/tutorialrequest")
@AllArgsConstructor
@SecurityScheme(name = "jwt-auth", type = SecuritySchemeType.HTTP, scheme = "bearer")
public class TutorialRequestController {

    private final TutorialRequestRepository tutorialRequestRepository;
    private final StudentService studentService;

    @Operation(
            tags={"tutorialRequest"},
            summary = "Create new TutorialRequest.",
            description = "Creates a new TutorialRequest for the logged in user as student.",
            security = @SecurityRequirement(name = "jwt-auth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successful creation."),
    })
    @PutMapping
    public ResponseEntity<Void> createTutorialOffer(@RequestBody CreateTutorialRequestRequest createTutorialRequestRequest) {

        TutorialRequest tutorialRequest = new TutorialRequest();

        tutorialRequest.setDescription(createTutorialRequestRequest.getDescription());
        tutorialRequest.setTitle(createTutorialRequestRequest.getTitle());
        tutorialRequest.setSemester(createTutorialRequestRequest.getSemester());

        // find out which user executes this operation
        Student student = studentService.getLoggedInStudent();
        if (student != null) {
            tutorialRequest.setCreatedBy(student);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        tutorialRequestRepository.save(tutorialRequest);

        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }
}
