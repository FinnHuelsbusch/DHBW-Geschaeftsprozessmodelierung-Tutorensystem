package com.dhbw.tutorsystem.tutorial;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.validation.Valid;

import com.dhbw.tutorsystem.exception.TSInternalServerException;
import com.dhbw.tutorsystem.mails.EmailSenderService;
import com.dhbw.tutorsystem.mails.MailType;
import com.dhbw.tutorsystem.security.authentication.exception.UserNotFoundException;
import com.dhbw.tutorsystem.specialisationCourse.SpecialisationCourse;
import com.dhbw.tutorsystem.specialisationCourse.SpecialisationCourseRepository;
import com.dhbw.tutorsystem.tutorial.exception.TSInvalidTutorialMarkException;
import com.dhbw.tutorsystem.tutorial.exception.TutorialNotFoundException;
import com.dhbw.tutorsystem.tutorial.payload.FindTutorialsWithFilterRequest;
import com.dhbw.tutorsystem.tutorial.payload.FindTutorialsWithFilterResponse;
import com.dhbw.tutorsystem.user.User;
import com.dhbw.tutorsystem.user.UserService;
import com.dhbw.tutorsystem.user.student.Student;
import com.dhbw.tutorsystem.user.student.StudentRepository;
import com.dhbw.tutorsystem.user.student.StudentService;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("tutorials")
public class TutorialController {

        @Autowired
        private SpecialisationCourseRepository specialisationCourseRepository;
        @Autowired
        private StudentService studentService;
        @Autowired
        private StudentRepository studentRepository;
        @Autowired
        private EmailSenderService emailSenderService;
        @Autowired
        private TutorialRepository tutorialRepository;
        @PersistenceUnit
        @Autowired
        private EntityManagerFactory entityManagerFactory;
        @Autowired
        ModelMapper modelMapper;

        @Operation(summary = "Find tutorial by id.", description = "Find a tutorial by its id.", tags = {
                        "tutorials" })
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Tutorial was found."),
                        @ApiResponse(responseCode = "400", description = "Path variable was not an integer."),
                        @ApiResponse(responseCode = "404", description = "Requested tutorial was not found."),
        })
        @GetMapping("/{id}")
        public ResponseEntity<TutorialDto> getTutorial(@PathVariable Integer id) {
                Optional<Tutorial> optionalTutorial = tutorialRepository.findById(id);
                if (optionalTutorial.isEmpty()) {
                        throw new TutorialNotFoundException();
                }
                Tutorial tutorial = optionalTutorial.get();
                TutorialDto tutorialDto = convertToDto(tutorial);
                Student student = studentService.getLoggedInStudent();
                // optionally add the student perspective attributes
                if (student != null) {
                        tutorialDto = tutorialDto.addPerspective(isMarkedByStudent(student, tutorial),
                                        isStudentParticipating(student, tutorial));
                }
                return ResponseEntity.ok(tutorialDto);
        }

        @Operation(summary = "Participate in tutorial.", description = "Participate in a tutorial by id.", tags = {
                        "tutorials" })
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Participation was successful."),
                        @ApiResponse(responseCode = "400", description = "Path variable was not an integer."),
                        @ApiResponse(responseCode = "404", description = "Requested tutorial was not found."),
        })
        @PutMapping("/participate/{id}")
        public ResponseEntity<Void> participateInTutorial(@PathVariable Integer id) {
                Optional<Tutorial> optionalTutorial = tutorialRepository.findById(id);
                if (optionalTutorial.isEmpty()) {
                        throw new TutorialNotFoundException();
                }
                Tutorial tutorial = optionalTutorial.get();
                // retrieve user and notify by email of the participation
                Student student = studentService.getLoggedInStudent();
                if (student == null) {
                        throw new UserNotFoundException();
                }
                if (isStudentParticipating(student, tutorial)) {
                        // user is not allowed to participate twice
                        throw new TSInternalServerException();
                }
                try {
                        emailSenderService.sendMail(student.getEmail(), MailType.TUTORIAL_PARTICIPATION,
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

        private boolean isStudentParticipating(Student student, Tutorial tutorial) {
                return student != null && tutorial != null
                                && tutorial.getParticipants() != null
                                && tutorial.getParticipants().stream().filter(s -> s.getId() == student.getId())
                                                .findFirst().isPresent();
        }

        private boolean isMarkedByStudent(Student student, Tutorial tutorial) {
                return student != null && tutorial != null
                                && tutorial.getMarkedBy() != null
                                && tutorial.getMarkedBy().stream().filter(s -> s.getId() == student.getId())
                                                .findFirst().isPresent();
        }

        @Operation(summary = "Mark a tutorial.", description = "Mark a tutorial that you are interested in using the id.", tags = {
                        "tutorials" })
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Marking was successful."),
                        @ApiResponse(responseCode = "400", description = "Path variable was not an integer or tutorial is already marked."),
                        @ApiResponse(responseCode = "404", description = "Requested tutorial was not found."),
        })
        @PutMapping("/mark/{id}")
        public ResponseEntity<Void> markTutorial(@PathVariable Integer id) {
                Optional<Tutorial> optionalTutorial = tutorialRepository.findById(id);
                if (optionalTutorial.isEmpty()) {
                        throw new TutorialNotFoundException();
                }
                Tutorial tutorial = optionalTutorial.get();
                Student student = studentService.getLoggedInStudent();
                if (student == null) {
                        throw new UserNotFoundException();
                }
                if (isMarkedByStudent(student, tutorial)) {
                        throw new TSInvalidTutorialMarkException("Tutorial is already marked");
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
                        @ApiResponse(responseCode = "400", description = "Path variable was not an integer."),
                        @ApiResponse(responseCode = "404", description = "Requested tutorial was not found."),
        })
        @DeleteMapping("/mark/{id}")
        public ResponseEntity<Void> unmarkTutorial(@PathVariable Integer id) {
                Optional<Tutorial> optionalTutorial = tutorialRepository.findById(id);
                if (optionalTutorial.isEmpty()) {
                        throw new TutorialNotFoundException();
                }
                Tutorial tutorial = optionalTutorial.get();
                Student student = studentService.getLoggedInStudent();
                if (student == null) {
                        throw new UserNotFoundException();
                }
                if (!isMarkedByStudent(student, tutorial)) {
                        throw new TSInvalidTutorialMarkException("Tutorial is already marked");
                }
                // associate student with tutorial, then tutorial with student
                if (tutorial.getMarkedBy() == null) {
                        throw new TSInternalServerException();
                } else {
                        tutorial.getMarkedBy().remove(student);
                }
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
        public ResponseEntity<FindTutorialsWithFilterResponse> findTutorialsWithFilter(Pageable pageable,
                        @Valid @RequestBody FindTutorialsWithFilterRequest filterRequest) {

                // construct filter conditions
                QTutorial tutorial = QTutorial.tutorial;

                // search text: any word matches title or description
                BooleanBuilder textMatches = new BooleanBuilder();
                if (filterRequest.getText() != null) {
                        List<String> textsToMatch = Arrays.asList(filterRequest.getText().split(" "));
                        for (String text : textsToMatch) {
                                text = "%" + text + "%";
                                textMatches.and(tutorial.title.likeIgnoreCase(text)
                                                .or(tutorial.description.likeIgnoreCase(text)));
                        }
                }

                // search specialisation course
                BooleanBuilder specialisationCourseMatches = new BooleanBuilder();
                if (filterRequest.getSpecialisationCourseIds() != null) {
                        Iterable<SpecialisationCourse> specialisationCourses = specialisationCourseRepository
                                        .findAllById((Iterable<Integer>) filterRequest.getSpecialisationCourseIds());
                        for (SpecialisationCourse specialisationCourse : specialisationCourses) {
                                specialisationCourseMatches
                                                .or(tutorial.specialisationCourses.contains(specialisationCourse));
                        }
                }

                // search start date within specified time range
                BooleanBuilder startsWithinTimeFrame = new BooleanBuilder();
                LocalDate defaultStartDateFrom = LocalDate.now().minusMonths(3);
                if (filterRequest.getStartDateFrom() == null &&
                                filterRequest.getStartDateTo() == null) {
                        // nothing specified, default to starting date 2 months in the past until
                        // infinity
                        startsWithinTimeFrame.and(tutorial.start.after(defaultStartDateFrom));
                } else if (filterRequest.getStartDateFrom() != null) {
                        // only from date specified
                        startsWithinTimeFrame.and(
                                        tutorial.start.after(filterRequest.getStartDateFrom()));
                } else if (filterRequest.getStartDateTo() != null) {
                        // only to date specified: default to starting date 2 months in the past until
                        // specified date
                        startsWithinTimeFrame.and(
                                        tutorial.start.between(defaultStartDateFrom,
                                                        filterRequest.getStartDateTo()));
                } else {
                        // from and to date specified
                        startsWithinTimeFrame.and(
                                        tutorial.start.between(filterRequest.getStartDateFrom(),
                                                        filterRequest.getStartDateTo()));
                }

                // construct pageable details
                int start = (int) pageable.getOffset();
                int size = pageable.getPageSize();
                if (size <= 0) {
                        size = 4; // default to 4 in size
                }

                EntityManager entityManager = entityManagerFactory.createEntityManager();
                JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
                entityManager.getTransaction().begin();

                // construct query from conditions, one for tutorial objects and one for total
                // element count
                JPAQuery<Tutorial> tutorialQuery = queryFactory
                                .selectFrom(tutorial)
                                .where(textMatches
                                                .and(specialisationCourseMatches)
                                                .and(startsWithinTimeFrame))
                                .offset(start)
                                .limit(size);

                JPAQuery<Long> countQuery = queryFactory
                                .from(tutorial)
                                .select(tutorial.id.countDistinct())
                                .where(textMatches
                                                .and(specialisationCourseMatches)
                                                .and(startsWithinTimeFrame));

                for (Sort.Order order : pageable.getSort()) {
                        com.querydsl.core.types.Order querydslOrder = order.isAscending()
                                        ? com.querydsl.core.types.Order.ASC
                                        : com.querydsl.core.types.Order.DESC;

                        if ("title".equals(order.getProperty())) {
                                tutorialQuery.orderBy(new OrderSpecifier<String>(
                                                querydslOrder,
                                                tutorial.title));
                        } else if ("start".equals(order.getProperty())) {
                                tutorialQuery.orderBy(new OrderSpecifier<LocalDate>(
                                                querydslOrder,
                                                tutorial.start));
                        }
                }

                List<Tutorial> filteredTutorials = tutorialQuery.fetch();
                int totalResultCount = countQuery.fetchFirst().intValue();

                for (Tutorial t : filteredTutorials) {
                        Hibernate.initialize(t.getSpecialisationCourses());
                        Hibernate.initialize(t.getTutors());
                        Hibernate.initialize(t.getParticipants());
                        Hibernate.initialize(t.getMarkedBy());
                }

                Student student = studentService.getLoggedInStudent();

                List<TutorialDto> tutorialDtos = filteredTutorials.stream().map(
                                t -> convertToDto(t).addPerspective(
                                                // perspective attributes only added if consumer is a logged-in student
                                                isMarkedByStudent(student, t),
                                                isStudentParticipating(student, t)))
                                .collect(Collectors.toList());

                entityManager.getTransaction().commit();
                entityManager.close();

                int totalPages = (int) Math.ceil((double) totalResultCount / pageable.getPageSize());

                return ResponseEntity.ok(new FindTutorialsWithFilterResponse(
                                tutorialDtos,
                                pageable.getPageNumber(),
                                totalPages,
                                totalResultCount));
        }

        private TutorialDto convertToDto(Tutorial tutorial) {
                TutorialDto tutorialDto = modelMapper.map(tutorial, TutorialDto.class);
                // map tutors to tutorDtos
                tutorialDto.setTutors(tutorial.getTutors().stream().map(tutor -> convertToDto(tutor))
                                .collect(Collectors.toSet()));
                int numberOfParticipants = tutorial.getParticipants() != null ? tutorial.getParticipants().size() : 0;
                tutorialDto.setNumberOfParticipants(numberOfParticipants);
                return tutorialDto;
        }

        private UserDto convertToDto(User user) {
                return modelMapper.map(user, UserDto.class);
        }

}
