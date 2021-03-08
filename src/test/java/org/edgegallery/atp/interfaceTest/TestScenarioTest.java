package org.edgegallery.atp.interfaceTest;

import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import org.apache.http.entity.ContentType;
import org.apache.ibatis.io.Resources;
import org.edgegallery.atp.ATPApplicationTest;
import org.edgegallery.atp.model.testscenario.TestScenario;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;
import com.google.gson.Gson;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ATPApplicationTest.class)
@AutoConfigureMockMvc
public class TestScenarioTest {
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
    public void testScenarioTest() throws Exception {
        // create scenario
        File file = Resources.getResourceAsFile("testfile/icon.png");
        InputStream iconInputStream = new FileInputStream(file);
        MultipartFile iconMultiFile = new MockMultipartFile(file.getName(), file.getName(),
                ContentType.APPLICATION_OCTET_STREAM.toString(), iconInputStream);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.multipart("/edgegallery/atp/v1/testscenarios")
                .file("icon", iconMultiFile.getBytes()).with(csrf()).param("nameEn", "testScenario")
                .param("nameCh", "testScenario").param("descriptionCh", "testScenario")
                .param("descriptionEn", "testScenario").param("label", "EdgeGallery")).andReturn();
        int result = mvcResult.getResponse().getStatus();
        assertEquals(200, result);

        String content = mvcResult.getResponse().getContentAsString();
        TestScenario testScenario = gson.fromJson(content, TestScenario.class);
        String id = testScenario.getId();

        // get icon file
        MvcResult mvcResultIconFile = mvc.perform(MockMvcRequestBuilders
                .get("/edgegallery/atp/v1/file/" + id + "?type =scenario").with(csrf())).andReturn();
        int resultIconFile = mvcResultIconFile.getResponse().getStatus();
        assertEquals(200, resultIconFile);

        // get one test scenario
        MvcResult mvcResultQueryOne = mvc.perform(MockMvcRequestBuilders.get("/edgegallery/atp/v1/testscenarios/" + id)
                .contentType(MediaType.APPLICATION_JSON_VALUE).with(csrf()).accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        int resultQueryOne = mvcResultQueryOne.getResponse().getStatus();
        assertEquals(200, resultQueryOne);

        // get all test scenarios
        MvcResult mvcResultQueryAll = mvc.perform(MockMvcRequestBuilders.get("/edgegallery/atp/v1/testscenarios")
                .contentType(MediaType.APPLICATION_JSON_VALUE).with(csrf()).accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        int resultQueryAll = mvcResultQueryAll.getResponse().getStatus();
        assertEquals(200, resultQueryAll);

        // get all test case under one test scenario
        MvcResult mvcResultQueryTestCases =
                mvc.perform(MockMvcRequestBuilders.multipart("/edgegallery/atp/v1/testscenarios/testcases").with(csrf())
                .param("scenarioIds", id)).andReturn();
        int resultQueryTestCases = mvcResultQueryTestCases.getResponse().getStatus();
        assertEquals(200, resultQueryTestCases);

        // delete
        MvcResult mvcResultDelete = mvc
                .perform(MockMvcRequestBuilders.delete("/edgegallery/atp/v1/testscenarios/" + id).with(csrf())
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        int resultDelete = mvcResultDelete.getResponse().getStatus();
        assertEquals(200, resultDelete);
    }
}
