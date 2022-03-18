package com.dhbw.tutorsystem.tutorial;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.mail.MessagingException;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.dhbw.tutorsystem.exception.TSBadRequestException;
import com.dhbw.tutorsystem.exception.TSExceptionResponse;
import com.dhbw.tutorsystem.exception.TSInternalServerException;
import com.dhbw.tutorsystem.mails.EmailSenderService;
import com.dhbw.tutorsystem.mails.MailType;
import com.dhbw.tutorsystem.specialisationCourse.SpecialisationCourseRepository;
import com.dhbw.tutorsystem.tutorial.dto.TutorialWithSpecialisationCoursesWithoutCourses;
import com.dhbw.tutorsystem.user.User;
import com.dhbw.tutorsystem.user.UserRepository;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/tutorial")
@AllArgsConstructor
@SecurityScheme(name = "jwt-auth", type = SecuritySchemeType.HTTP, scheme = "bearer")
public class TutorialController {

    private final TutorialRepository tutorialRepository;
    private final UserRepository userRepository;
    private final EmailSenderService emailSenderService;
    private final SpecialisationCourseRepository specialisationCourseRepository;
    private final ModelMapper modelMapper;

    @Operation(tags = {
            "tutorial" }, summary = "Get all tutorials.", description = "Get all tutorials. ", security = @SecurityRequirement(name = "jwt-auth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns all Tutorials orderd and filterd."),
    })
    @GetMapping(params = { "pageNumber", "pageSize", "sortBy" })
    public ResponseEntity<List<Tutorial>> getAllTutorials(
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "id") String sortBy) {
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));

        Page<Tutorial> pagedResult = tutorialRepository.findAll(paging);

        if (pagedResult.hasContent()) {
            return new ResponseEntity<List<Tutorial>>(pagedResult.getContent(), HttpStatus.OK);
        } else {
            return new ResponseEntity<List<Tutorial>>(new ArrayList<Tutorial>(), HttpStatus.OK);
        }

    }

    @Operation(tags = {
            "tutorial" }, summary = "Get one specific tutorials.", description = "Get one specific tutorial by a tutorial ID. ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns the tutorial."),
            @ApiResponse(responseCode = "400", description = "A tutorial with the given ID does not exist", content = @Content(schema = @Schema(implementation = TSExceptionResponse.class)))

    })
    @GetMapping(params = { "ID" })
    public ResponseEntity<Tutorial> getTutorial(
            @RequestParam Integer ID) {
        Optional<Tutorial> optionalTutorial = tutorialRepository.findById(ID);
        if (optionalTutorial.isEmpty()) {
            throw new TSBadRequestException("The tutorial with the given ID does not exist");
        } else {
            return new ResponseEntity<>(optionalTutorial.get(), HttpStatus.OK);
        }

    }

    @Operation(tags = {
            "tutorial" }, summary = "Create new tutorial.", description = "Create a new tutorial and get it as return.", security = @SecurityRequirement(name = "jwt-auth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Returns the created tutorial."),
            @ApiResponse(responseCode = "400", description = "One of the parameters was not set correctly.", content = @Content(schema = @Schema(implementation = TSExceptionResponse.class)))

    })
    @PutMapping()
    @PreAuthorize("hasRole('ROLE_DIRECTOR')")
    public ResponseEntity<TutorialWithSpecialisationCoursesWithoutCourses> createTutorial(
            @RequestBody @NotNull @Valid CreateTutorialRequest createTutorialRequest) {

        if (createTutorialRequest.getStart().isBefore(LocalDate.now()) ||
                createTutorialRequest.getStart().isAfter(createTutorialRequest.getEnd())) {
            throw new TSBadRequestException("The start and end are not valid. Earliest start is today.");
        }

        if (!specialisationCourseRepository.existsByIdIn(createTutorialRequest.getSpecialisationCoursesIds())) {
            throw new TSBadRequestException("One of the specialisationCourses does not exist");
        }

        Tutorial tutorial = new Tutorial();

        HashMap<String, Object> mailArguments = new HashMap<>();
        mailArguments.put("tutorialTitle", createTutorialRequest.getTitle());
        mailArguments.put("tutorialDescription", createTutorialRequest.getDescription());
        mailArguments.put("tutorialStart", createTutorialRequest.getStart());
        mailArguments.put("tutorialEnd", createTutorialRequest.getEnd());
        mailArguments.put("tutorialDurationMinutes", createTutorialRequest.getDurationMinutes());
        mailArguments.put("tutorialTutorEmails", createTutorialRequest.getTutorEmails());

        List<User> tutors = handleAddedTutors(createTutorialRequest.getTutorEmails(), mailArguments);

        if(tutors.size() > 0){
            
        }

        tutorial.setTutors(Set.copyOf(tutors));
        tutorial.setDescription(createTutorialRequest.getDescription());
        tutorial.setTitle(createTutorialRequest.getTitle());
        tutorial.setStart(createTutorialRequest.getStart());
        tutorial.setEnd(createTutorialRequest.getEnd());
        tutorial.setDurationMinutes(createTutorialRequest.getDurationMinutes());
        tutorial.setSpecialisationCourses(
                specialisationCourseRepository.findAllById(createTutorialRequest.getSpecialisationCoursesIds()));
        tutorial.setAppointment(createTutorialRequest.getAppointment());

        tutorial = tutorialRepository.save(tutorial);

        return new ResponseEntity<>(TutorialWithSpecialisationCoursesWithoutCourses.convertToDto(modelMapper, tutorial),
                HttpStatus.CREATED);
    }

    private List<User> handleAddedTutors(Set<String> tutorMails, Map<String, Object> mailArguments) {
        List<User> usersToReturn = new ArrayList<>();
        for (String tutorEmail : tutorMails) {
            Optional<User> optionalUser = userRepository.findByEmail(tutorEmail);
            User user;
            // if user is not registered yet create a new user and send a notification
            if (optionalUser.isPresent()) {
                user = optionalUser.get();
                try {
                    emailSenderService.sendMail(user.getEmail(), MailType.USER_ADDED_TO_TUTORIAL, mailArguments);
                } catch (MessagingException e) {
                    throw new TSInternalServerException();
                }
            } else {
                user = new User();
                user.setEmail(tutorEmail);
                try {
                    emailSenderService.sendMail(user.getEmail(), MailType.UNREGISTERD_USER_ADDED_TO_TUTORIAL,
                            mailArguments);
                } catch (MessagingException e) {
                    throw new TSInternalServerException();
                }
            }
            usersToReturn.add(user);
        }
        return userRepository.saveAll(usersToReturn);
    }

    @Operation(tags = {
            "tutorial" }, summary = "Delete one specific tutorial.", description = "Delete one specific tutorial by a tutorial ID. ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tutorial was deleted"),
            @ApiResponse(responseCode = "400", description = "A tutorial with the given ID does not exist", content = @Content(schema = @Schema(implementation = TSExceptionResponse.class)))
    })
    @DeleteMapping(params = { "ID" })
    public ResponseEntity<Void> deleteTutorial(
            @RequestParam Integer ID) {
        Optional<Tutorial> optionalTutorial = tutorialRepository.findById(ID);
        if (optionalTutorial.isEmpty()) {
            throw new TSBadRequestException("The tutorial with the given ID does not exist");
        } else {
            tutorialRepository.deleteById(ID);
            return new ResponseEntity<>(HttpStatus.OK);
        }

    }

}
