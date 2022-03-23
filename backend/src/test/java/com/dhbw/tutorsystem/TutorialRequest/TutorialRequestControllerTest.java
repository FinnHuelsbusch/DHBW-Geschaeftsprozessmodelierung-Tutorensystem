package com.dhbw.tutorsystem.TutorialRequest;

import static com.dhbw.tutorsystem.utils.RequestType.PUT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.dhbw.tutorsystem.tutorialRequest.CreateTutorialRequestRequest;
import com.dhbw.tutorsystem.tutorialRequest.TutorialRequest;
import com.dhbw.tutorsystem.tutorialRequest.TutorialRequestRepository;
import com.dhbw.tutorsystem.utils.MvcTestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TutorialRequestControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private TutorialRequestRepository tutorialRequestRepository;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private CreateTutorialRequestRequest tutorialRequest;

    private MvcTestUtils mvcUtils;

    @BeforeAll
    private void init() {
        mvcUtils = new MvcTestUtils("/tutorialrequest", mvc, objectMapper);
    }

    @BeforeEach
    private void createTutorialRequest() {
        tutorialRequestRepository.deleteAll();
        //mvcUtils = new MvcTestUtils("/tutorialrequest", mvc, objectMapper);
        CreateTutorialRequestRequest tutorialRequest = new CreateTutorialRequestRequest();
        tutorialRequest.setTitle("Programmieren I");
        tutorialRequest.setDescription("Ich brauche Hilfe bei Datenstrukturen");
        tutorialRequest.setSemester(3);
        this.tutorialRequest = tutorialRequest;
    }

    @Test
    @WithMockUser(username = "s111111@student.dhbw-mannheim.de", password = "1234", roles = "STUDENT")
    void createValidTutorialRequest() throws Exception {
        mvcUtils.perform(PUT, "/", tutorialRequest, CREATED);
    }

    @Test
    @Transactional
    @WithMockUser(username = "s111111@student.dhbw-mannheim.de", password = "1234", roles = "STUDENT")
    void checkIfRequestIsInDatabase() throws Exception {
        long beforeAddingRequest = tutorialRequestRepository.count();
        mvcUtils.perform(PUT, "/", tutorialRequest, CREATED);
        // no API route, get requests via repository
        TutorialRequest tutorialRequest = tutorialRequestRepository.findAll().iterator().next();
        assertEquals("Programmieren I", tutorialRequest.getTitle());
        assertEquals("Ich brauche Hilfe bei Datenstrukturen", tutorialRequest.getDescription());
        assertEquals("s111111@student.dhbw-mannheim.de", tutorialRequest.getCreatedBy().getEmail());
        assertEquals(3, tutorialRequest.getSemester());
        assertEquals(1, (tutorialRequestRepository.count() - beforeAddingRequest));
    }

    @Test
    @WithMockUser(username = "s111111@student.dhbw-mannheim.de", password = "1234", roles = "STUDENT")
    void semesterOutOfLowerBound() throws Exception {
        tutorialRequest.setSemester(0);
        mvcUtils.performExpectException(PUT, "/", tutorialRequest, BAD_REQUEST,
                "Field 'semester' has error: must be greater than or equal to 1.",
                MethodArgumentNotValidException.class);
    }

    @Test
    @WithMockUser(username = "s111111@student.dhbw-mannheim.de", password = "1234", roles = "STUDENT")
    void semesterOutOfUpperBound() throws Exception {
        tutorialRequest.setSemester(7);
        mvcUtils.performExpectException(PUT, "/", tutorialRequest, BAD_REQUEST,
                "Field 'semester' has error: must be less than or equal to 6.",
                MethodArgumentNotValidException.class);
    }

    @Test
    @WithMockUser(username = "s111111@student.dhbw-mannheim.de", password = "1234", roles = "STUDENT")
    void semesterNegativeNumber() throws Exception {
        tutorialRequest.setSemester(-1);
        mvcUtils.performExpectException(PUT, "/", tutorialRequest, BAD_REQUEST,
                "Field 'semester' has error: must be greater than or equal to 1.",
                MethodArgumentNotValidException.class);

    }

    @Test
    @WithMockUser(username = "s111111@student.dhbw-mannheim.de", password = "1234", roles = "STUDENT")
    void semesterNotPresent() throws Exception {
        tutorialRequest.setSemester(null);
        mvcUtils.performExpectException(PUT, "/", tutorialRequest, BAD_REQUEST,
                "Field 'semester' has error: must not be null.",
                MethodArgumentNotValidException.class);
    }

    @Test
    @WithMockUser(username = "s111111@student.dhbw-mannheim.de", password = "1234", roles = "STUDENT")
    void descriptionEmpty() throws Exception {
        tutorialRequest.setDescription("");
        mvcUtils.performExpectException(PUT, "/", tutorialRequest, BAD_REQUEST,
                "Field 'description' has error: must not be blank.",
                MethodArgumentNotValidException.class);
    }

    @Test
    @WithMockUser(username = "s111111@student.dhbw-mannheim.de", password = "1234", roles = "STUDENT")
    void descriptionNotPresent() throws Exception {
        CreateTutorialRequestRequest request = new CreateTutorialRequestRequest();
        request.setTitle("Programmieren I");
        request.setSemester(4);
        mvcUtils.performExpectException(PUT, "/", request, BAD_REQUEST,
                "Field 'description' has error: must not be blank.",
                MethodArgumentNotValidException.class);
    }

    @Test
    @WithMockUser(username = "s111111@student.dhbw-mannheim.de", password = "1234", roles = "STUDENT")
    void titleEmpty() throws Exception {
        tutorialRequest.setTitle("");
        mvcUtils.performExpectException(PUT, "/", tutorialRequest, BAD_REQUEST,
                "Field 'title' has error: must not be blank.",
                MethodArgumentNotValidException.class);
    }

    @Test
    @WithMockUser(username = "s111111@student.dhbw-mannheim.de", password = "1234", roles = "STUDENT")
    void titleNotPresent() throws Exception {
        tutorialRequest.setTitle(null);
        mvcUtils.performExpectException(PUT, "/", tutorialRequest, BAD_REQUEST,
                "Field 'title' has error: must not be blank.",
                MethodArgumentNotValidException.class);
    }

    @Test
    void notLoggedIn() throws Exception {
        mvcUtils.perform(PUT, "/", tutorialRequest, UNAUTHORIZED);
    }

    @Test
    @WithMockUser(username = "adam.admin@dhbw-mannheim.de", password = "1234", roles = "ADMIN")
    void loggedInAsAdmin() throws Exception {
        mvcUtils.performExpectException(PUT, "/", tutorialRequest, INTERNAL_SERVER_ERROR,
                "Access is denied",
                AccessDeniedException.class);
    }

    @Test
    @WithMockUser(username = "dirk.director@dhbw-mannheim.de", password = "1234", roles = "DIRECTOR")
    void loggedInAsDirector() throws Exception {
        mvcUtils.performExpectException(PUT, "/", tutorialRequest, INTERNAL_SERVER_ERROR,
                "Access is denied", AccessDeniedException.class);
    }
}