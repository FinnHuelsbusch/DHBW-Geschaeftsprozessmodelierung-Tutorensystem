package com.dhbw.tutorsystem.Course;

import static com.dhbw.tutorsystem.utils.RequestType.GET;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.OK;

import com.dhbw.tutorsystem.course.CourseRepository;
import com.dhbw.tutorsystem.specialisationCourse.SpecialisationCourseRepository;
import com.dhbw.tutorsystem.tutorial.TutorialRepository;
import com.dhbw.tutorsystem.utils.JsonPayloadTestUtils;
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
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CourseControllerTest {

    @Autowired
    private MockMvc mvc;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private MvcTestUtils mvcUtils;
    private JsonPayloadTestUtils jsonUtils;

    @BeforeAll
    private void init() {
        mvcUtils = new MvcTestUtils("/courses", mvc, objectMapper);
        jsonUtils = new JsonPayloadTestUtils("/Course", objectMapper);
    }

    @Test
    void getCoursesWithTitleAndLeadersNotLoggedIn() throws Exception {
        MvcResult result =  mvcUtils.perform(GET, "/withTitleAndLeaders", OK);
        jsonUtils.assertPayloadMatches("withTitleAndLeaders.json", result.getResponse().getContentAsString());
    }

    @Test
    void getCoursesWithTitleAndSpecialisationsNotLoggedIn() throws Exception {
        MvcResult result =  mvcUtils.perform(GET, "/withTitleAndSpecialisations", OK);
        jsonUtils.assertPayloadMatches("withTitleAndSpecialisations.json", result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(username = "s111111@student.dhbw-mannheim.de", password = "1234", roles = "STUDENT")
    void getCoursesWithTitleAndLeadersAsStudent() throws Exception {
        MvcResult result =  mvcUtils.perform(GET, "/withTitleAndLeaders", OK);
        jsonUtils.assertPayloadMatches("withTitleAndLeaders.json", result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(username = "s111111@student.dhbw-mannheim.de", password = "1234", roles = "STUDENT")
    void getCoursesWithTitleAndSpecialisationsAsStudent() throws Exception {
        MvcResult result =  mvcUtils.perform(GET, "/withTitleAndSpecialisations", OK);
        jsonUtils.assertPayloadMatches("withTitleAndSpecialisations.json", result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(username = "adam.admin@dhbw-mannheim.de", password = "1234", roles = "ADMIN")
    void getCoursesWithTitleAndLeadersAsAdmin() throws Exception {
        MvcResult result =  mvcUtils.perform(GET, "/withTitleAndLeaders", OK);
        jsonUtils.assertPayloadMatches("withTitleAndLeaders.json", result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(username = "adam.admin@dhbw-mannheim.de", password = "1234", roles = "ADMIN")
    void getCoursesWithTitleAndSpecialisationsAsAdmin() throws Exception {
        MvcResult result =  mvcUtils.perform(GET, "/withTitleAndSpecialisations", OK);
        jsonUtils.assertPayloadMatches("withTitleAndSpecialisations.json", result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(username = "dirk.director@dhbw-mannheim.de", password = "1234", roles = "DIRECTOR")
    void getCoursesWithTitleAndLeadersAsDirector() throws Exception {
        MvcResult result =  mvcUtils.perform(GET, "/withTitleAndLeaders", OK);
        jsonUtils.assertPayloadMatches("withTitleAndLeaders.json", result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(username = "dirk.director@dhbw-mannheim.de", password = "1234", roles = "DIRECTOR")
    void getCoursesWithTitleAndSpecialisationsAsDirector() throws Exception {
        MvcResult result =  mvcUtils.perform(GET, "/withTitleAndSpecialisations", OK);
        jsonUtils.assertPayloadMatches("withTitleAndSpecialisations.json", result.getResponse().getContentAsString());
    }
}