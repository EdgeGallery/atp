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
import org.edgegallery.atp.model.testcase.TestCase;
import org.edgegallery.atp.model.testcase.TestCaseResult;
import org.edgegallery.atp.utils.JavaCompileUtil;
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
public class TestCaseTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private Gson gson = new Gson();

    @Before
    public void setUp() throws Exception {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).apply(springSecurity()).build();
    }

    @WithMockUser(roles = "ATP_TENANT")
    @Test
    public void getAllTestCases() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/edgegallery/atp/v1/testcases")
                .contentType(MediaType.APPLICATION_JSON_VALUE).with(csrf()).accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        int result = mvcResult.getResponse().getStatus();
        assertEquals(200, result);
    }

    @WithMockUser(roles = "ATP_TENANT")
    @Test
    public void getAllTestCaesWithParameters() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/edgegallery/atp/v1/testcases")
                .param("verificationModel", "EdgeGallery")
            .contentType(MediaType.APPLICATION_JSON_VALUE).with(csrf()).accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        int result = mvcResult.getResponse().getStatus();
        assertEquals(200, result);
    }

    @WithMockUser(roles = "ATP_TENANT")
    @Test
    public void getOneTestCaesWithParameters() throws Exception {
        MvcResult mvcResult = mvc
                .perform(
                        MockMvcRequestBuilders.get("/edgegallery/atp/v1/testcases/4d203173-2222-4f62-aabb-8ebcec357f87")
                                .with(csrf()).accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        int result = mvcResult.getResponse().getStatus();
        assertEquals(200, result);
    }

    @WithMockUser(roles = "ATP_TENANT")
    @Test
    public void TestCaseTest() throws Exception {
        File file = Resources.getResourceAsFile("testfile/Test.java");
        InputStream csarInputStream = new FileInputStream(file);
        MultipartFile csarMultiFile = new MockMultipartFile(file.getName(), file.getName(),
                ContentType.APPLICATION_OCTET_STREAM.toString(), csarInputStream);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.multipart("/edgegallery/atp/v1/testcases")
                .file("file", csarMultiFile.getBytes()).with(csrf()).param("name", "test")
                .param("type", "complianceTest").param("description", "test").param("codeLanguage", "java")
                .param("expectResult", "test").param("verificationModel", "EdgeGallery")).andReturn();
        int result = mvcResult.getResponse().getStatus();
        assertEquals(200, result);

        String content = mvcResult.getResponse().getContentAsString();
        TestCase testCase = gson.fromJson(content, TestCase.class);
        String id = testCase.getId();

        // compile
        JavaCompileUtil compile = new JavaCompileUtil();
        TestCaseResult resultTestCase = new TestCaseResult();
        testCase.setClassName("Test");
        testCase.setFilePath("testfile/Test.java");
        compile.executeJava(testCase, "testfile/AR.csar", resultTestCase, null);

        // delete
        MvcResult mvcResultDelete = mvc
                .perform(MockMvcRequestBuilders.delete("/edgegallery/atp/v1/testcases/" + id).with(csrf())
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        int resultDelete = mvcResultDelete.getResponse().getStatus();
        assertEquals(200, resultDelete);
    }

}
