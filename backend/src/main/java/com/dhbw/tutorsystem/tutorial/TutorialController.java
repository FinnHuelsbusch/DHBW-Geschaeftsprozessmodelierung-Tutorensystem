package com.dhbw.tutorsystem.tutorial;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.mail.MessagingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.dhbw.tutorsystem.exception.TSExceptionResponse;
import com.dhbw.tutorsystem.exception.TSInternalServerException;
import com.dhbw.tutorsystem.mails.EmailSenderService;
import com.dhbw.tutorsystem.mails.MailType;
import com.dhbw.tutorsystem.security.authentication.exception.InvalidEmailException;
import com.dhbw.tutorsystem.specialisationCourse.SpecialisationCourse;
import com.dhbw.tutorsystem.specialisationCourse.SpecialisationCourseRepository;
import com.dhbw.tutorsystem.tutorial.dto.CreateTutorialRequest;
import com.dhbw.tutorsystem.tutorial.dto.TutorialForDisplay;
import com.dhbw.tutorsystem.tutorial.dto.TutorialWithSpecialisationCoursesWithoutCourses;
import com.dhbw.tutorsystem.tutorial.exception.InvalidTutorialMarkException;
import com.dhbw.tutorsystem.tutorial.exception.SpecialisationCourseNotFoundException;
import com.dhbw.tutorsystem.tutorial.exception.InvalidTutorialParticipationStatusException;
import com.dhbw.tutorsystem.tutorial.exception.TutorialInvalidTimerangeException;
import com.dhbw.tutorsystem.tutorial.exception.TutorialNotFoundException;
import com.dhbw.tutorsystem.tutorial.payload.DeleteTutorialRequest;
import com.dhbw.tutorsystem.tutorial.payload.FindTutorialsWithFilterRequest;
import com.dhbw.tutorsystem.tutorial.payload.FindTutorialsWithFilterResponse;
import com.dhbw.tutorsystem.user.User;
import com.dhbw.tutorsystem.user.UserRepository;
import com.dhbw.tutorsystem.user.student.Student;
import com.dhbw.tutorsystem.user.student.StudentRepository;
import com.dhbw.tutorsystem.user.student.StudentService;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.hibernate.HibernateQuery;
import com.querydsl.jpa.hibernate.HibernateQueryFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("tutorials")
@RequiredArgsConstructor
@SecurityScheme(name = "jwt-auth", type = SecuritySchemeType.HTTP, scheme = "bearer")
public class TutorialController {

    private final SpecialisationCourseRepository specialisationCourseRepository;
    private final StudentService studentService;
    private final StudentRepository studentRepository;
    private final EmailSenderService emailSenderService;
    private final TutorialRepository tutorialRepository;
    @PersistenceUnit
    private final EntityManagerFactory entityManagerFactory;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    @Operation(summary = "Find tutorial by id.", description = "Find a tutorial by its id.", tags = {
            "tutorials" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tutorial was found."),
            @ApiResponse(responseCode = "400", description = "Path variable was not an integer.", content = @Content(schema = @Schema(implementation = TSExceptionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Requested tutorial was not found.", content = @Content(schema = @Schema(implementation = TSExceptionResponse.class))),
    })
    @GetMapping("/{id}")
    public ResponseEntity<TutorialForDisplay> getTutorial(@PathVariable Integer id) {
        Optional<Tutorial> optionalTutorial = tutorialRepository.findById(id);
        if (optionalTutorial.isEmpty()) {
            throw new TutorialNotFoundException();
        }
        Tutorial tutorial = optionalTutorial.get();
        TutorialForDisplay tutorialDto = TutorialForDisplay.convertToDto(modelMapper, tutorial);
        Student student = studentService.getLoggedInStudent();
        // optionally add the student perspective attributes
        if (student != null) {
            tutorialDto = tutorialDto.addPerspective(
                    tutorial.isMarkedByStudent(student),
                    tutorial.isStudentParticipating(student),
                    tutorial.isHeldByStudent(student));
        }
        return ResponseEntity.ok(tutorialDto);
    }

    @Operation(summary = "Participate in tutorial.", description = "Participate in a tutorial by id.", tags = {
            "tutorials" }, security = @SecurityRequirement(name = "jwt-auth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Participation was successful."),
            @ApiResponse(responseCode = "400", description = "Path variable was not an integer.", content = @Content(schema = @Schema(implementation = TSExceptionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Requested tutorial was not found.", content = @Content(schema = @Schema(implementation = TSExceptionResponse.class))),
    })
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @PutMapping("/participate/{id}")
    public ResponseEntity<Void> participateInTutorial(@PathVariable Integer id) {
        Optional<Tutorial> optionalTutorial = tutorialRepository.findById(id);
        if (optionalTutorial.isEmpty()) {
            throw new TutorialNotFoundException();
        }
        Tutorial tutorial = optionalTutorial.get();
        // retrieve user and notify by email of the participation
        // note: email does not have to be checked, logged in user is already valid
        Student student = studentService.getLoggedInStudent();
        if (tutorial.isStudentParticipating(student)) {
            throw new InvalidTutorialParticipationStatusException("Student is already participating.");
        }
        try {
            emailSenderService.sendMail(student.getEmail(), MailType.TUTORIAL_PARTICIPATION_STUDENT,
                    Map.of("tutorialTitle", tutorial.getTitle(),
                            "tutorialId", tutorial.getId()));
        } catch (MessagingException e) {
            throw new TSInternalServerException();
        }
        // add user as participant of this tutorial
        tutorial.getParticipants().add(student);
        tutorialRepository.save(tutorial);
        return ResponseEntity.ok(null);
    }

    @Operation(summary = "Remove participation in tutorial.", description = "Remove participation in a tutorial by id.", tags = {
            "tutorials" }, security = @SecurityRequirement(name = "jwt-auth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Participation was successfully removed."),
            @ApiResponse(responseCode = "400", description = "Path variable was not an integer.", content = @Content(schema = @Schema(implementation = TSExceptionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Requested tutorial was not found.", content = @Content(schema = @Schema(implementation = TSExceptionResponse.class))),
    })
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @DeleteMapping("/participate/{id}")
    public ResponseEntity<Void> removeParticipationInTutorial(@PathVariable Integer id) {
        Optional<Tutorial> optionalTutorial = tutorialRepository.findById(id);
        if (optionalTutorial.isEmpty()) {
            throw new TutorialNotFoundException();
        }
        Tutorial tutorial = optionalTutorial.get();
        // notify user of the removal of participation
        // note: email does not have to be checked, logged in user is already valid
        Student student = studentService.getLoggedInStudent();
        if (!tutorial.isStudentParticipating(student)) {
            throw new InvalidTutorialParticipationStatusException("Student is already not participating.");
        }
        try {
            emailSenderService.sendMail(student.getEmail(), MailType.TUTORIAL_PARTICIPATION_REMOVAL_STUDENT,
                    Map.of("tutorialTitle", tutorial.getTitle()));
        } catch (MessagingException e) {
            throw new TSInternalServerException();
        }
        // remove user participation
        tutorial.getParticipants().remove(student);
        tutorialRepository.save(tutorial);
        return ResponseEntity.ok(null);
    }

    @Operation(summary = "Mark a tutorial.", description = "Mark a tutorial that you are interested in using the id.", tags = {
            "tutorials" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Marking was successful."),
            @ApiResponse(responseCode = "400", description = "Path variable was not an integer or tutorial is already marked.", content = @Content(schema = @Schema(implementation = TSExceptionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Requested tutorial was not found.", content = @Content(schema = @Schema(implementation = TSExceptionResponse.class))),
    })
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @PutMapping("/mark/{id}")
    public ResponseEntity<Void> markTutorial(@PathVariable Integer id) {
        Optional<Tutorial> optionalTutorial = tutorialRepository.findById(id);
        if (optionalTutorial.isEmpty()) {
            throw new TutorialNotFoundException();
        }
        Tutorial tutorial = optionalTutorial.get();
        Student student = studentService.getLoggedInStudent();
        if (tutorial.isMarkedByStudent(student)) {
            throw new InvalidTutorialMarkException("Tutorial is already marked.");
        }
        // associate student with tutorial, then tutorial with student
        if (tutorial.getMarkedBy() == null) {
            tutorial.setMarkedBy(Set.of(student));
        } else {
            tutorial.getMarkedBy().add(student);
        }
        tutorial = tutorialRepository.save(tutorial);

        if (student.getMarkedTutorials() == null) {
            student.setMarkedTutorials(Set.of(tutorial));
        } else {
            student.getMarkedTutorials().add(tutorial);
        }
        studentRepository.save(student);

        return ResponseEntity.ok(null);
    }

    @Operation(summary = "Unmark a tutorial.", description = "Unmark a tutorial that you are not anymore interested in using the id.", tags = {
            "tutorials" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Unmarking was successful."),
            @ApiResponse(responseCode = "400", description = "Path variable was not an integer.", content = @Content(schema = @Schema(implementation = TSExceptionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Requested tutorial was not found.", content = @Content(schema = @Schema(implementation = TSExceptionResponse.class))),
    })
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @DeleteMapping("/mark/{id}")
    public ResponseEntity<Void> unmarkTutorial(@PathVariable Integer id) {
        Optional<Tutorial> optionalTutorial = tutorialRepository.findById(id);
        if (optionalTutorial.isEmpty()) {
            throw new TutorialNotFoundException();
        }
        Tutorial tutorial = optionalTutorial.get();
        Student student = studentService.getLoggedInStudent();
        if (!tutorial.isMarkedByStudent(student)) {
            throw new InvalidTutorialMarkException("Tutorial is already unmarked.");
        }
        // associate student with tutorial, then tutorial with student
        tutorial.getMarkedBy().remove(student);
        tutorial = tutorialRepository.save(tutorial);

        if (student.getMarkedTutorials() == null) {
            throw new TSInternalServerException();
        } else {
            student.getMarkedTutorials().remove(tutorial);
        }
        studentRepository.save(student);

        return ResponseEntity.ok(null);
    }

    @Operation(summary = "Query tutorials with filter", description = "Query tutorials by supplying a specified paging configuration and a filter, that contains text, specialisation courses and a time range for the start date.", tags = {
            "tutorials" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login was successful. User is logged by using the token in the response."),
    })
    @PostMapping("/findWithFilter")
    @Transactional
    public ResponseEntity<FindTutorialsWithFilterResponse> findTutorialsWithFilter(Pageable pageable,
            @Valid @RequestBody FindTutorialsWithFilterRequest filterRequest) {

        // construct filter conditions
        QTutorial qTutorial = QTutorial.tutorial;

        // search text: any word matches title or description
        BooleanBuilder textMatches = new BooleanBuilder();
        if (filterRequest.getText() != null) {
            List<String> textsToMatch = Arrays.asList(filterRequest.getText().split(" "));
            for (String text : textsToMatch) {
                text = "%" + text + "%";
                textMatches.and(qTutorial.title.likeIgnoreCase(text)
                        .or(qTutorial.description.likeIgnoreCase(text)));
            }
        }

        // search specialisation course
        BooleanBuilder specialisationCourseMatches = new BooleanBuilder();
        if (filterRequest.getSpecialisationCourseIds() != null) {
            Set<SpecialisationCourse> specialisationCourses = specialisationCourseRepository
                    .findAllById(filterRequest.getSpecialisationCourseIds());
            for (SpecialisationCourse specialisationCourse : specialisationCourses) {
                specialisationCourseMatches
                        .or(qTutorial.specialisationCourses.contains(specialisationCourse));
            }
        }

        // search start date within specified time range
        BooleanBuilder startsWithinTimeFrame = new BooleanBuilder();
        LocalDate defaultStartDateFrom = LocalDate.now().minusMonths(3);
        if (filterRequest.getStartDateFrom() != null &&
                filterRequest.getStartDateTo() != null) {
            // from and to date specified
            startsWithinTimeFrame.and(
                    qTutorial.start.between(filterRequest.getStartDateFrom(),
                            filterRequest.getStartDateTo()));
        } else if (filterRequest.getStartDateFrom() != null) {
            // only from date specified
            startsWithinTimeFrame.and(
                    qTutorial.start.after(filterRequest.getStartDateFrom()));
        } else if (filterRequest.getStartDateTo() != null) {
            // only to date specified: default to starting date 2 months in the past until
            // specified date
            startsWithinTimeFrame.and(
                    qTutorial.start.between(defaultStartDateFrom,
                            filterRequest.getStartDateTo()));
        } else {
            // nothing specified, default to starting date 2 months in the past until
            // infinity
            startsWithinTimeFrame.and(qTutorial.start.after(defaultStartDateFrom));
        }

        // apply further criteria for logged in students
        Student loggedStudent = studentService.getLoggedInStudent();
        BooleanBuilder isMarked = new BooleanBuilder();
        BooleanBuilder isParticipating = new BooleanBuilder();
        BooleanBuilder isHolds = new BooleanBuilder();
        if (loggedStudent != null) {
            if (filterRequest.isSelectMarked()) {
                // apply restriction to show marked courses
                isMarked.and(qTutorial.markedBy.contains(loggedStudent));
            }
            if (filterRequest.isSelectParticipates()) {
                // apply restriction to show participate courses
                isParticipating.and(qTutorial.participants.contains(loggedStudent));
            }
            if (filterRequest.isSelectHolds()) {
                // apply restriction to show held courses
                isHolds.and(qTutorial.tutors.contains(loggedStudent));
            }
        }

        // construct pageable details
        int start = (int) pageable.getOffset();
        int size = pageable.getPageSize();
        if (size <= 0) {
            size = 4; // default to 4 in size
        }

        Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession();
        HibernateQueryFactory queryFactory = new HibernateQueryFactory(session);

        // construct query from conditions, one for tutorial objects and one for total
        // element count
        // note: empty BooleanBuilder will be ignored in query
        HibernateQuery<Tutorial> tutorialQuery = queryFactory
                .selectFrom(qTutorial)
                .where(textMatches
                        .and(specialisationCourseMatches)
                        .and(startsWithinTimeFrame)
                        .and(isMarked)
                        .and(isParticipating)
                        .and(isHolds))
                .offset(start)
                .limit(size);

        // note: this count query must have same conditions as the above query
        HibernateQuery<Long> countQuery = queryFactory
                .from(qTutorial)
                .select(qTutorial.id.countDistinct())
                .where(textMatches
                        .and(specialisationCourseMatches)
                        .and(startsWithinTimeFrame)
                        .and(isMarked)
                        .and(isParticipating)
                        .and(isHolds));

        for (Sort.Order order : pageable.getSort()) {
            com.querydsl.core.types.Order querydslOrder = order.isAscending()
                    ? com.querydsl.core.types.Order.ASC
                    : com.querydsl.core.types.Order.DESC;

            if ("title".equals(order.getProperty())) {
                tutorialQuery.orderBy(new OrderSpecifier<String>(
                        querydslOrder,
                        qTutorial.title));
            } else if ("start".equals(order.getProperty())) {
                tutorialQuery.orderBy(new OrderSpecifier<LocalDate>(
                        querydslOrder,
                        qTutorial.start));
            }
        }

        List<Tutorial> filteredTutorials = tutorialQuery.fetch();
        int totalResultCount = countQuery.fetchFirst().intValue();

        Student student = studentService.getLoggedInStudent();

        List<TutorialForDisplay> tutorialDtos = filteredTutorials.stream().map(
                t -> TutorialForDisplay.convertToDto(modelMapper, t).addPerspective(
                        // perspective attributes only added if consumer is a logged-in student
                        t.isMarkedByStudent(student),
                        t.isStudentParticipating(student),
                        t.isHeldByStudent(student)))
                .collect(Collectors.toList());

        session.close();

        int totalPages = (int) Math.ceil((double) totalResultCount / pageable.getPageSize());

        return ResponseEntity.ok(new FindTutorialsWithFilterResponse(
                tutorialDtos,
                pageable.getPageNumber(),
                totalPages,
                totalResultCount));
    }

    @Operation(tags = {
            "tutorial" }, summary = "Create new tutorial.", description = "Create a new tutorial and get it as return.", security = @SecurityRequirement(name = "jwt-auth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Returns the created tutorial."),
            @ApiResponse(responseCode = "400", description = "One of the parameters was not set correctly.", content = @Content(schema = @Schema(implementation = TSExceptionResponse.class)))
    })
    @PutMapping
    @PreAuthorize("hasRole('ROLE_DIRECTOR')")
    public ResponseEntity<TutorialWithSpecialisationCoursesWithoutCourses> createTutorial(
            @RequestBody @NotNull @Valid CreateTutorialRequest createTutorialRequest) {

        if (createTutorialRequest.getStart().isBefore(LocalDate.now()) ||
                createTutorialRequest.getStart().isAfter(createTutorialRequest.getEnd())) {
            throw new TutorialInvalidTimerangeException();
        }
        if (!specialisationCourseRepository.existsByIdIn(createTutorialRequest.getSpecialisationCoursesIds())) {
            throw new SpecialisationCourseNotFoundException();
        }
        if (!createTutorialRequest.getTutorEmails().stream().allMatch(email -> User.isValidEmail(email))) {
            throw new InvalidEmailException();
        }

        Set<User> tutors = handleAddedTutors(createTutorialRequest.getTutorEmails(),
                Map.of(
                        "tutorialTitle", createTutorialRequest.getTitle(),
                        "tutorialDescription", createTutorialRequest.getDescription(),
                        "tutorialStart", createTutorialRequest.getStart(),
                        "tutorialEnd", createTutorialRequest.getEnd(),
                        "tutorialDurationMinutes", createTutorialRequest.getDurationMinutes(),
                        "tutorialTutorEmails", createTutorialRequest.getTutorEmails()));

        Tutorial tutorial = new Tutorial();
        mapCreateTutorialRequestToTutorial(createTutorialRequest, tutors, tutorial);

        tutorial = tutorialRepository.save(tutorial);

        return new ResponseEntity<>(
                TutorialWithSpecialisationCoursesWithoutCourses.convertToDto(modelMapper, tutorial),
                HttpStatus.CREATED);
    }

    private Set<User> handleAddedTutors(Set<String> tutorMails, Map<String, Object> mailArguments) {
        List<User> usersToReturn = new ArrayList<>();
        for (String tutorEmail : tutorMails) {
            Optional<User> optionalUser = userRepository.findByEmail(tutorEmail);
            User user;
            // if user is not registered yet create a new user and send a notification
            if (optionalUser.isPresent()) {
                user = optionalUser.get();
                try {
                    emailSenderService.sendMail(user.getEmail(), MailType.TUTORIAL_TUTOR_ADDED,
                            mailArguments);
                } catch (MessagingException e) {
                    throw new TSInternalServerException();
                }
            } else {
                user = new User();
                user.setEmail(tutorEmail);
                try {
                    emailSenderService.sendMail(user.getEmail(),
                            MailType.UNREGISTERD_TUTORIAL_TUTOR_ADDED,
                            mailArguments);
                } catch (MessagingException e) {
                    throw new TSInternalServerException();
                }
            }
            usersToReturn.add(user);
        }
        return new HashSet<>(userRepository.saveAll(usersToReturn));
    }

    @Operation(tags = {
            "tutorial" }, summary = "Update an tutorial.", description = "Update a new tutorial and get it as return.", security = @SecurityRequirement(name = "jwt-auth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "20o", description = "Returns the updated tutorial."),
            @ApiResponse(responseCode = "400", description = "One of the parameters was not set correctly.", content = @Content(schema = @Schema(implementation = TSExceptionResponse.class)))
    })
    @PostMapping("/update/{id}")
    @PreAuthorize("hasRole('ROLE_DIRECTOR')")
    public ResponseEntity<TutorialWithSpecialisationCoursesWithoutCourses> updateTutorial(
            @RequestBody @NotNull @Valid CreateTutorialRequest createTutorialRequest, @PathVariable int id) {

        if (createTutorialRequest.getStart().isBefore(LocalDate.now()) ||
                createTutorialRequest.getStart().isAfter(createTutorialRequest.getEnd())) {
            throw new TutorialInvalidTimerangeException();
        }
        if (!specialisationCourseRepository.existsByIdIn(createTutorialRequest.getSpecialisationCoursesIds())) {
            throw new SpecialisationCourseNotFoundException();
        }
        if (!createTutorialRequest.getTutorEmails().stream().allMatch(email -> User.isValidEmail(email))) {
            throw new InvalidEmailException();
        }

        Map<String, Object> emailAttributes = Map.of(
                "tutorialTitle", createTutorialRequest.getTitle(),
                "tutorialDescription", createTutorialRequest.getDescription(),
                "tutorialStart", createTutorialRequest.getStart(),
                "tutorialEnd", createTutorialRequest.getEnd(),
                "tutorialDurationMinutes", createTutorialRequest.getDurationMinutes());

        Tutorial tutorial = tutorialRepository.findById(id).orElseThrow(TutorialNotFoundException::new);

        Set<String> oldTutorEmails = tutorial.getTutors().stream().map(tutor -> tutor.getEmail())
                .collect(Collectors.toSet());

        Set<User> tutors = Set.of();
        // Mail to all new tutors
        Set<String> addedTutorsMails = new HashSet<>(createTutorialRequest.getTutorEmails());
        addedTutorsMails.removeAll(oldTutorEmails);
        if (!addedTutorsMails.isEmpty()) {
            tutors = handleAddedTutors(addedTutorsMails, emailAttributes);
        }
        try {
            // Mail to all removed Tutors
            Set<String> removedTutorsMails = new HashSet<>(oldTutorEmails);
            removedTutorsMails.removeAll(createTutorialRequest.getTutorEmails()); 
            if (!removedTutorsMails.isEmpty()) {
                emailSenderService.sendMails(removedTutorsMails, MailType.TUTORIAL_TUTOR_REMOVED, emailAttributes);
            }

            // Notify users over changed Tutorial
            Set<String> remainingTutorsMails = new HashSet<>(createTutorialRequest.getTutorEmails());
            remainingTutorsMails.retainAll(oldTutorEmails);
            if (!remainingTutorsMails.isEmpty()) {
                emailSenderService.sendMails(remainingTutorsMails, MailType.TUTORIAL_CHANGED, emailAttributes);
            }
        } catch (MessagingException e) {
            throw new TSInternalServerException();
        }

        mapCreateTutorialRequestToTutorial(createTutorialRequest,userRepository.findAllByEmailIn(createTutorialRequest.getTutorEmails()), tutorial);

        tutorialRepository.save(tutorial);
        return new ResponseEntity<>(
                TutorialWithSpecialisationCoursesWithoutCourses.convertToDto(modelMapper, tutorial),
                HttpStatus.OK);
    }

    private void mapCreateTutorialRequestToTutorial(CreateTutorialRequest createTutorialRequest, Set<User> tutors,
            Tutorial tutorial) {
        tutorial.setTutors(new HashSet<>(tutors));
        tutorial.setDescription(createTutorialRequest.getDescription());
        tutorial.setTitle(createTutorialRequest.getTitle());
        tutorial.setStart(createTutorialRequest.getStart());
        tutorial.setEnd(createTutorialRequest.getEnd());
        tutorial.setDurationMinutes(createTutorialRequest.getDurationMinutes());
        tutorial.setSpecialisationCourses(specialisationCourseRepository
                .findAllById(createTutorialRequest.getSpecialisationCoursesIds()));
        tutorial.setAppointment(createTutorialRequest.getAppointment());
        return;
    }

    @Operation(tags = {
            "tutorial" }, summary = "Delete one specific tutorial.", description = "Delete one specific tutorial by a tutorial ID. ", security = @SecurityRequirement(name = "jwt-auth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tutorial was deleted"),
            @ApiResponse(responseCode = "400", description = "A tutorial with the given ID does not exist", content = @Content(schema = @Schema(implementation = TSExceptionResponse.class)))
    })
    @PostMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_DIRECTOR')")
    public ResponseEntity<Void> deleteTutorial(@PathVariable Integer id,
            @RequestBody @Valid DeleteTutorialRequest deleteTutorialRequest) {
        Optional<Tutorial> optionalTutorial = tutorialRepository.findById(id);
        String reason = deleteTutorialRequest.getReason();
        if (optionalTutorial.isEmpty()) {
            throw new TutorialNotFoundException();
        } else {
            Tutorial tutorial = optionalTutorial.get();
            Map<String, Object> mailArguments = Map.of(
                    "tutorialTitle", tutorial.getTitle(),
                    "tutorialDescription", tutorial.getDescription(),
                    "tutorialStart", tutorial.getStart(),
                    "tutorialEnd", tutorial.getEnd(),
                    "tutorialDurationMinutes", tutorial.getDurationMinutes(),
                    "reason", reason != null? reason : "");
            try {
                emailSenderService.sendMails(
                        tutorial.getTutors().stream().map(tutor -> tutor.getEmail())
                                .collect(Collectors.toSet()),
                        MailType.TUTORIAL_DELETION, mailArguments);

                emailSenderService.sendMails(
                        tutorial.getParticipants().stream().map(tutor -> tutor.getEmail())
                                .collect(Collectors.toSet()),
                        MailType.TUTORIAL_DELETION, mailArguments);
            } catch (MessagingException e) {
                throw new TSInternalServerException();
            }
            tutorialRepository.deleteById(id);

            return new ResponseEntity<>(HttpStatus.OK);
        }
    }
}