package com.dhbw.tutorsystem.Course;

import static com.dhbw.tutorsystem.utils.RequestType.GET;
import static org.springframework.http.HttpStatus.OK;
import com.dhbw.tutorsystem.utils.MvcTestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CourseControllerTest {

    @Autowired
    private MockMvc mvc;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private MvcTestUtils mvcUtils;

    @BeforeAll
    private void init() {
        mvcUtils = new MvcTestUtils("/courses", mvc, objectMapper);
    }

    @Test
    void getCoursesWithTitleAndLeadersNotLoggedIn() throws Exception {
        mvcUtils.perform(GET, "/withTitleAndLeaders", null, OK);
    }

    @Test
    void getCoursesWithTitleAndSpecialisationsNotLoggedIn() throws Exception {
        mvcUtils.perform(GET, "/withTitleAndSpecialisations", null, OK);
    }

    @Test
    @WithMockUser(username = "s111111@student.dhbw-mannheim.de", password = "1234", roles = "STUDENT")
    void getCoursesWithTitleAndLeadersAsStudent() throws Exception {
        mvcUtils.perform(GET, "/withTitleAndLeaders", null, OK);
    }

    @Test
    @WithMockUser(username = "s111111@student.dhbw-mannheim.de", password = "1234", roles = "STUDENT")
    void getCoursesWithTitleAndSpecialisationsAsStudent() throws Exception {
        mvcUtils.perform(GET, "/withTitleAndSpecialisations", null, OK);
    }

    @Test
    @WithMockUser(username = "adam.admin@dhbw-mannheim.de", password = "1234", roles = "ADMIN")
    void getCoursesWithTitleAndLeadersAsAdmin() throws Exception {
        mvcUtils.perform(GET, "/withTitleAndLeaders", null, OK);
    }

    @Test
    @WithMockUser(username = "adam.admin@dhbw-mannheim.de", password = "1234", roles = "ADMIN")
    void getCoursesWithTitleAndSpecialisationsAsAdmin() throws Exception {
        mvcUtils.perform(GET, "/withTitleAndSpecialisations", null, OK);
    }

    @Test
    @WithMockUser(username = "dirk.director@dhbw-mannheim.de", password = "1234", roles = "DIRECTOR")
    void getCoursesWithTitleAndLeadersAsDirector() throws Exception {
        mvcUtils.perform(GET, "/withTitleAndLeaders", null, OK);
    }

    @Test
    @WithMockUser(username = "dirk.director@dhbw-mannheim.de", password = "1234", roles = "DIRECTOR")
    void getCoursesWithTitleAndSpecialisationsAsDirector() throws Exception {
        mvcUtils.perform(GET, "/withTitleAndSpecialisations", null, OK);
    }
}