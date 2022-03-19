package com.dhbw.tutorsystem.ping;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PingControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void ping() throws Exception {
       MvcResult responsePing = mvc.perform(get("/ping"))
                            .andExpect(status().isOk())
                            .andReturn();
        System.out.println(responsePing.getResponse().getContentAsString());
    }

    @Test
    void pingAuthStudent() throws Exception {
        mvc.perform(get("/ping/auth-student"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles="STUDENT")
    void pingAuthStudentWithUser() throws Exception {
        mvc.perform(get("/ping/auth-student"))
                .andExpect(status().isOk());
    }


    @Test
    void pingAuthAdmin() throws Exception {
        mvc.perform(get("/ping/auth-admin"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles="ADMIN")
    void pingAuthAdminWithUser() throws Exception {
        mvc.perform(get("/ping/auth-admin"))
                .andExpect(status().isOk());
    }

    @Test
    void pingAuthDirector() throws Exception {
        mvc.perform(get("/ping/auth-director"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles="DIRECTOR")
    void pingAuthDirectorWithUser() throws Exception {
        mvc.perform(get("/ping/auth-director"))
                .andExpect(status().isOk());
    }
}