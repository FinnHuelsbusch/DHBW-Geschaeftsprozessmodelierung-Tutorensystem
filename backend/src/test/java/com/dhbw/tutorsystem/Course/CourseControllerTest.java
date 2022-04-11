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

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private SpecialisationCourseRepository specialisationCourseRepository;

    @Autowired
    private TutorialRepository tutorialRepository;

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String withTitleAndLeaders = "[{\"id\":1,\"title\":\"Wirtschaftsinformatik\",\"abbreviation\":\"WI\",\"leadBy\":[{\"firstName\":\"Dirk\",\"lastName\":\"Director\",\"email\":\"dirk.director@dhbw-mannheim.de\"},{\"firstName\":\"Daniel\",\"lastName\":\"Director\",\"email\":\"daniel.director@dhbw-mannheim.de\"}]},{\"id\":2,\"title\":\"Maschinenbau\",\"abbreviation\":\"MB\",\"leadBy\":[{\"firstName\":\"Doris\",\"lastName\":\"Director\",\"email\":\"doris.director@dhbw-mannheim.de\"}]}]"; 
    private static final String withTitleAndSpecialisations = "[{\"id\":1,\"title\":\"Wirtschaftsinformatik\",\"abbreviation\":\"WI\",\"specialisationCourses\":[{\"id\":2,\"title\":\"Sales and Consulting\",\"abbreviation\":\"SC\"},{\"id\":1,\"title\":\"Software Engineering\",\"abbreviation\":\"SE\"}]},{\"id\":2,\"title\":\"Maschinenbau\",\"abbreviation\":\"MB\",\"specialisationCourses\":[{\"id\":3,\"title\":\"Allgemeiner Maschinenbau\",\"abbreviation\":\"AMB\"}]}]";

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
        jsonUtils.assertPayloadMatches("test1.json", result.getResponse().getContentAsString());
    }

    @Test
    void getCoursesWithTitleAndSpecialisationsNotLoggedIn() throws Exception {
        MvcResult result =  mvcUtils.perform(GET, "/withTitleAndSpecialisations", OK);
        assertEquals(withTitleAndSpecialisations, result.getResponse().getContentAsString());
    }

    @Test
    void getCoursesWithTitleAndLeadersNotLoggedInEmpty() throws Exception {
        tutorialRepository.deleteAll();
        specialisationCourseRepository.deleteAll();
        courseRepository.deleteAll();
        MvcResult result =  mvcUtils.perform(GET, "/withTitleAndLeaders", OK);
        assertEquals(withTitleAndLeaders, result.getResponse().getContentAsString());
    }

    @Test
    void getCoursesWithTitleAndSpecialisationsNotLoggedInEmpty() throws Exception {
        MvcResult result =  mvcUtils.perform(GET, "/withTitleAndSpecialisations", OK);
        assertEquals(withTitleAndSpecialisations, result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(username = "s111111@student.dhbw-mannheim.de", password = "1234", roles = "STUDENT")
    void getCoursesWithTitleAndLeadersAsStudent() throws Exception {
        MvcResult result =  mvcUtils.perform(GET, "/withTitleAndLeaders", OK);
        assertEquals(withTitleAndLeaders, result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(username = "s111111@student.dhbw-mannheim.de", password = "1234", roles = "STUDENT")
    void getCoursesWithTitleAndSpecialisationsAsStudent() throws Exception {
        MvcResult result =  mvcUtils.perform(GET, "/withTitleAndSpecialisations", OK);
        assertEquals(withTitleAndSpecialisations, result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(username = "adam.admin@dhbw-mannheim.de", password = "1234", roles = "ADMIN")
    void getCoursesWithTitleAndLeadersAsAdmin() throws Exception {
        MvcResult result =  mvcUtils.perform(GET, "/withTitleAndLeaders", OK);
        assertEquals(withTitleAndLeaders, result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(username = "adam.admin@dhbw-mannheim.de", password = "1234", roles = "ADMIN")
    void getCoursesWithTitleAndSpecialisationsAsAdmin() throws Exception {
        MvcResult result =  mvcUtils.perform(GET, "/withTitleAndSpecialisations", OK);
        assertEquals(withTitleAndSpecialisations, result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(username = "dirk.director@dhbw-mannheim.de", password = "1234", roles = "DIRECTOR")
    void getCoursesWithTitleAndLeadersAsDirector() throws Exception {
        MvcResult result =  mvcUtils.perform(GET, "/withTitleAndLeaders", OK);
        assertEquals(withTitleAndLeaders, result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(username = "dirk.director@dhbw-mannheim.de", password = "1234", roles = "DIRECTOR")
    void getCoursesWithTitleAndSpecialisationsAsDirector() throws Exception {
        MvcResult result =  mvcUtils.perform(GET, "/withTitleAndSpecialisations", OK);
        assertEquals(withTitleAndSpecialisations, result.getResponse().getContentAsString());
    }
}