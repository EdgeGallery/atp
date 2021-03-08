package org.edgegallery.atp.interfaceTest;

import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import org.edgegallery.atp.ATPApplicationTest;
import org.edgegallery.atp.model.testsuite.TestSuite;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import com.google.gson.Gson;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ATPApplicationTest.class)
@AutoConfigureMockMvc
public class TestSuiteTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private Gson gson = new Gson();

    @Before
    public void setUp() throws Exception {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).apply(springSecurity()).build();
    }

    @WithMockUser(roles = "ATP_ADMIN")
    @Test
    public void testSuiteTest() throws Exception {
        // create test suite
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.multipart("/edgegallery/atp/v1/testsuites")
                .with(csrf()).param("nameEn", "testSuite").param("nameCh", "testSuite")
                .param("descriptionCh", "testSuite").param("descriptionEn", "testSuite")
                .param("scenarioIdList", "4d203111-1111-4f62-aabb-8ebcec357f87")).andReturn();
        int result = mvcResult.getResponse().getStatus();
        assertEquals(200, result);

        String content = mvcResult.getResponse().getContentAsString();
        TestSuite testSuite = gson.fromJson(content, TestSuite.class);
        String id = testSuite.getId();

        // get one test suite
        MvcResult mvcResultQueryOne = mvc.perform(MockMvcRequestBuilders.get("/edgegallery/atp/v1/testsuites/" + id)
                .contentType(MediaType.APPLICATION_JSON_VALUE).with(csrf()).accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        int resultQueryOne = mvcResultQueryOne.getResponse().getStatus();
        assertEquals(200, resultQueryOne);

        // get all test suites
        MvcResult mvcResultQueryAll = mvc.perform(MockMvcRequestBuilders.get("/edgegallery/atp/v1/testsuites")
                .contentType(MediaType.APPLICATION_JSON_VALUE).with(csrf()).accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        int resultQueryAll = mvcResultQueryAll.getResponse().getStatus();
        assertEquals(200, resultQueryAll);

        // delete
        MvcResult mvcResultDelete = mvc
                .perform(MockMvcRequestBuilders.delete("/edgegallery/atp/v1/testsuites/" + id).with(csrf())
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        int resultDelete = mvcResultDelete.getResponse().getStatus();
        assertEquals(200, resultDelete);
    }
}
