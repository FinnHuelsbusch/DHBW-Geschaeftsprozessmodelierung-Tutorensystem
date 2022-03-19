package com.dhbw.tutorsystem.TutorialRequest;

import com.dhbw.tutorsystem.security.authentication.exception.StudentNotLoggedInException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.dhbw.tutorsystem.tutorialRequest.CreateTutorialRequestRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class TutorialRequestControllerTest {

    @Autowired
    private MockMvc mvc;
    
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private CreateTutorialRequestRequest createTutorialRequest(){
        CreateTutorialRequestRequest tutorialRequest = new CreateTutorialRequestRequest();
        tutorialRequest.setTitle("Programmieren I");
        tutorialRequest.setDescription("Ich brauche Hilfe bei Datenstrukturen");
        tutorialRequest.setSemester(3);
        return tutorialRequest;
    }

    @Test
    @WithMockUser(username="s111111@student.dhbw-mannheim.de",password = "1234",roles="STUDENT")
    void createValidTutorialRequest() throws Exception {
        String putValue = objectMapper.writeValueAsString(createTutorialRequest());
        mvc.perform(put("/tutorialrequest").contentType(MediaType.APPLICATION_JSON).content(putValue))
            .andExpect(status().isCreated())
            .andDo(print())
            .andReturn();
    }

    @Test
    @WithMockUser(username="s111111@student.dhbw-mannheim.de",password = "1234",roles="STUDENT")
    void semesterOutOfLowerBound() throws Exception {
        CreateTutorialRequestRequest request = createTutorialRequest();
        request.setSemester(0);
        String putValue = objectMapper.writeValueAsString(request);
        mvc.perform(put("/tutorialrequest").contentType(MediaType.APPLICATION_JSON).content(putValue))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn();
    }

    @Test
    @WithMockUser(username="s111111@student.dhbw-mannheim.de",password = "1234",roles="STUDENT")
    void semesterOutOfUpperBound() throws Exception {
        CreateTutorialRequestRequest request = createTutorialRequest();
        request.setSemester(7);
        String putValue = objectMapper.writeValueAsString(request);
        mvc.perform(put("/tutorialrequest").contentType(MediaType.APPLICATION_JSON).content(putValue))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn();
    }

    @Test
    @WithMockUser(username="s111111@student.dhbw-mannheim.de",password = "1234",roles="STUDENT")
    void semesterNegativeNumber() throws Exception {
        CreateTutorialRequestRequest request = createTutorialRequest();
        request.setSemester(-1);
        String putValue = objectMapper.writeValueAsString(request);
        mvc.perform(put("/tutorialrequest").contentType(MediaType.APPLICATION_JSON).content(putValue))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn();
    }

    @Test
    @WithMockUser(username="s111111@student.dhbw-mannheim.de",password = "1234",roles="STUDENT")
    void semesterNotPresent() throws Exception {
        CreateTutorialRequestRequest request = new CreateTutorialRequestRequest();
        request.setTitle("Programmieren I");
        request.setDescription("Ich brauche Hilfe bei Datenstrukturen.");
        String putValue = objectMapper.writeValueAsString(request);
        mvc.perform(put("/tutorialrequest").contentType(MediaType.APPLICATION_JSON).content(putValue))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn();
    }

    @Test
    @WithMockUser(username="s111111@student.dhbw-mannheim.de",password = "1234",roles="STUDENT")
    void descriptionEmpty() throws Exception {
        CreateTutorialRequestRequest request = createTutorialRequest();
        request.setDescription("");
        String putValue = objectMapper.writeValueAsString(request);
        mvc.perform(put("/tutorialrequest").contentType(MediaType.APPLICATION_JSON).content(putValue))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn();
    }

    @Test
    @WithMockUser(username="s111111@student.dhbw-mannheim.de",password = "1234",roles="STUDENT")
    void descriptionNotPresent() throws Exception {
        CreateTutorialRequestRequest request = new CreateTutorialRequestRequest();
        request.setTitle("Programmieren I");
        request.setSemester(4);
        String putValue = objectMapper.writeValueAsString(request);
        mvc.perform(put("/tutorialrequest").contentType(MediaType.APPLICATION_JSON).content(putValue))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn();
    }

    @Test
    @WithMockUser(username="s111111@student.dhbw-mannheim.de",password = "1234",roles="STUDENT")
    void titleEmpty() throws Exception {
        CreateTutorialRequestRequest request = createTutorialRequest();
        request.setTitle("");
        String putValue = objectMapper.writeValueAsString(request);
        mvc.perform(put("/tutorialrequest").contentType(MediaType.APPLICATION_JSON).content(putValue))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn();
    }

    @Test
    @WithMockUser(username="s111111@student.dhbw-mannheim.de",password = "1234",roles="STUDENT")
    void titleNotPresent() throws Exception {
        CreateTutorialRequestRequest request = new CreateTutorialRequestRequest();
        request.setDescription("Ich brauche Hilfe bei Datenstrukturen.");
        request.setSemester(4);
        String putValue = objectMapper.writeValueAsString(request);
        mvc.perform(put("/tutorialrequest").contentType(MediaType.APPLICATION_JSON).content(putValue))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn();
    }

    @Test
    void notLoggedIn() throws Exception{
        String putValue = objectMapper.writeValueAsString(createTutorialRequest());
        MvcResult a = mvc.perform(put("/tutorialrequest").contentType(MediaType.APPLICATION_JSON).content(putValue))
            .andExpect(status().isUnauthorized())
            //.andExpect(result -> {assertTrue(result.getResolvedException() instanceof StudentNotLoggedInException);})
            //.andExpect(jsonPath("errorCode").value("STUDENT_NOT_LOGGED_IN"))
            //.andDo(print())
           .andReturn();
        Throwable b = a.getResolvedException();
        System.out.println(a.getResolvedException());
    }

    @Test
    @WithMockUser(username="adam.admin@dhbw-mannheim.de",password = "1234",roles="ADMIN") 
    void loggedInAsAdmin() throws Exception{
        String putValue = objectMapper.writeValueAsString(createTutorialRequest());
        mvc.perform(put("/tutorialrequest").contentType(MediaType.APPLICATION_JSON).content(putValue))
             .andExpect(status().isInternalServerError())
             .andDo(print())
             .andReturn();
    }

    @Test
    @WithMockUser(username="dirk.director@dhbw-mannheim.de",password = "1234",roles="DIRECTOR") 
    void loggedInAsDirector() throws Exception{
        String putValue = objectMapper.writeValueAsString(createTutorialRequest());
        mvc.perform(put("/tutorialrequest").contentType(MediaType.APPLICATION_JSON).content(putValue))
            .andExpect(status().isInternalServerError())
            .andDo(print())
            .andReturn();
    }
}