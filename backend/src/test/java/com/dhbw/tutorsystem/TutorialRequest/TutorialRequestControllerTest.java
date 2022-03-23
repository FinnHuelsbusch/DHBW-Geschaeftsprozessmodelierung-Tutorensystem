package com.dhbw.tutorsystem.TutorialRequest;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dhbw.tutorsystem.tutorialRequest.CreateTutorialRequestRequest;
import com.dhbw.tutorsystem.tutorialRequest.TutorialRequest;
import com.dhbw.tutorsystem.tutorialRequest.TutorialRequestRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;

@SpringBootTest
@AutoConfigureMockMvc
class TutorialRequestControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private TutorialRequestRepository tutorialRequestRepository;
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final ResultMatcher BAD_REQUEST = status().isBadRequest();
    private static final ResultMatcher UNAUTHORIZED = status().isUnauthorized();
    private static final ResultMatcher CREATED = status().isCreated();
    private static final ResultMatcher INTERNAL_SERVER_ERROR = status().isInternalServerError();
    private CreateTutorialRequestRequest tutorialRequest;

    @BeforeEach
    private void  createTutorialRequest(){
        tutorialRequestRepository.deleteAll();
        CreateTutorialRequestRequest tutorialRequest = new CreateTutorialRequestRequest();
        tutorialRequest.setTitle("Programmieren I");
        tutorialRequest.setDescription("Ich brauche Hilfe bei Datenstrukturen");
        tutorialRequest.setSemester(3);
        this.tutorialRequest = tutorialRequest;
    }

    private void performMVC (CreateTutorialRequestRequest request, ResultMatcher expectedHttpResponseValue) throws Exception{
        performMVC(request, expectedHttpResponseValue, null, null);
    }

    private void performMVC (CreateTutorialRequestRequest request, ResultMatcher expectedHttpResponseValue, String expectedMessage, Class<?> expectedErrorClass) throws Exception{
        mvc.perform(put("/tutorialrequest").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
                .andExpect(expectedHttpResponseValue)
                .andExpect(tempResult -> {if(expectedErrorClass != null){ assertTrue( expectedErrorClass.isInstance(tempResult.getResolvedException()));}})
                .andExpect(tempResult -> {if(expectedMessage != null){ assertEquals(expectedMessage, new JSONObject(tempResult.getResponse().getContentAsString()).getString("message"));}})
                .andDo(print())
                .andReturn();
    }

    @Test
    @WithMockUser(username="s111111@student.dhbw-mannheim.de",password = "1234",roles="STUDENT")
    void createValidTutorialRequest() throws Exception {
        performMVC(tutorialRequest, CREATED);
    }

    @Test
    @Transactional
    @WithMockUser(username="s111111@student.dhbw-mannheim.de",password = "1234",roles="STUDENT")
    void checkIfRequestIsInDatabase() throws Exception {
        long beforeAddingRequest = tutorialRequestRepository.count();
        performMVC(tutorialRequest, CREATED);
        //no API route, get requests via repository
        TutorialRequest tutorialRequest = tutorialRequestRepository.findAll().iterator().next();
        assertEquals("Programmieren I",tutorialRequest.getTitle());
        assertEquals("Ich brauche Hilfe bei Datenstrukturen", tutorialRequest.getDescription());
        assertEquals("s111111@student.dhbw-mannheim.de",tutorialRequest.getCreatedBy().getEmail());
        assertEquals(3, tutorialRequest.getSemester());
        assertEquals(1, (tutorialRequestRepository.count()-beforeAddingRequest));
    }

    @Test
    @WithMockUser(username="s111111@student.dhbw-mannheim.de",password = "1234",roles="STUDENT")
    void semesterOutOfLowerBound() throws Exception {
        tutorialRequest.setSemester(0);
        performMVC(tutorialRequest, BAD_REQUEST, "Field 'semester' has error: must be greater than or equal to 1.", MethodArgumentNotValidException.class);
    }

    @Test
    @WithMockUser(username="s111111@student.dhbw-mannheim.de",password = "1234",roles="STUDENT")
    void semesterOutOfUpperBound() throws Exception {
        tutorialRequest.setSemester(7);
        performMVC(tutorialRequest, BAD_REQUEST, "Field 'semester' has error: must be less than or equal to 6.", MethodArgumentNotValidException.class);
    }

    @Test
    @WithMockUser(username="s111111@student.dhbw-mannheim.de",password = "1234",roles="STUDENT")
    void semesterNegativeNumber() throws Exception {
        tutorialRequest.setSemester(-1);
        performMVC(tutorialRequest, BAD_REQUEST, "Field 'semester' has error: must be greater than or equal to 1.", MethodArgumentNotValidException.class);
    }

    @Test
    @WithMockUser(username="s111111@student.dhbw-mannheim.de",password = "1234",roles="STUDENT")
    void semesterNotPresent() throws Exception {
        CreateTutorialRequestRequest request = new CreateTutorialRequestRequest();
        request.setTitle("Programmieren I");
        request.setDescription("Ich brauche Hilfe bei Datenstrukturen.");
        performMVC(request, BAD_REQUEST, "Field 'semester' has error: must be greater than or equal to 1.", MethodArgumentNotValidException.class);
    }

    @Test
    @WithMockUser(username="s111111@student.dhbw-mannheim.de",password = "1234",roles="STUDENT")
    void descriptionEmpty() throws Exception {
        tutorialRequest.setDescription("");
        performMVC(tutorialRequest, BAD_REQUEST, "Field 'description' has error: must not be blank.", MethodArgumentNotValidException.class);
    }

    @Test
    @WithMockUser(username="s111111@student.dhbw-mannheim.de",password = "1234",roles="STUDENT")
    void descriptionNotPresent() throws Exception {
        CreateTutorialRequestRequest request = new CreateTutorialRequestRequest();
        request.setTitle("Programmieren I");
        request.setSemester(4);
        performMVC(request, BAD_REQUEST, "Field 'description' has error: must not be blank.", MethodArgumentNotValidException.class);
    }

    @Test
    @WithMockUser(username="s111111@student.dhbw-mannheim.de",password = "1234",roles="STUDENT")
    void titleEmpty() throws Exception {
        tutorialRequest.setTitle("");
        performMVC(tutorialRequest, BAD_REQUEST, "Field 'title' has error: must not be blank.", MethodArgumentNotValidException.class);
    }

    @Test
    @WithMockUser(username="s111111@student.dhbw-mannheim.de",password = "1234",roles="STUDENT")
    void titleNotPresent() throws Exception {
        CreateTutorialRequestRequest request = new CreateTutorialRequestRequest();
        request.setDescription("Ich brauche Hilfe bei Datenstrukturen.");
        request.setSemester(4);
        performMVC(request, BAD_REQUEST, "Field 'title' has error: must not be blank.", MethodArgumentNotValidException.class);
    }

    @Test
    void notLoggedIn() throws Exception{
        performMVC(tutorialRequest, UNAUTHORIZED);
    }

    @Test
    @WithMockUser(username="adam.admin@dhbw-mannheim.de",password = "1234",roles="ADMIN") 
    void loggedInAsAdmin() throws Exception{
        performMVC(tutorialRequest, INTERNAL_SERVER_ERROR, "Access is denied", AccessDeniedException.class);
    }

    @Test
    @WithMockUser(username="dirk.director@dhbw-mannheim.de",password = "1234",roles="DIRECTOR") 
    void loggedInAsDirector() throws Exception{
        performMVC(tutorialRequest, INTERNAL_SERVER_ERROR, "Access is denied", AccessDeniedException.class);
    }
}