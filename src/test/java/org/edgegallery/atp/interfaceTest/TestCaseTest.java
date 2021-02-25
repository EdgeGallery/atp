package org.edgegallery.atp.interfaceTest;

import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.io.FileUtils;
import org.apache.http.entity.ContentType;
import org.apache.ibatis.io.Resources;
import org.edgegallery.atp.ATPApplicationTest;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.model.task.testscenarios.TaskTestCase;
import org.edgegallery.atp.model.testcase.TestCase;
import org.edgegallery.atp.model.testcase.TestCaseResult;
import org.edgegallery.atp.utils.FileChecker;
import org.edgegallery.atp.utils.JarCallUtil;
import org.edgegallery.atp.utils.JavaCompileUtil;
import org.edgegallery.atp.utils.PythonCallUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.python.util.PythonInterpreter;
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
import org.springframework.util.FileCopyUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;
import com.google.gson.Gson;
import mockit.Mock;
import mockit.MockUp;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ATPApplicationTest.class)
@AutoConfigureMockMvc
public class TestCaseTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private Gson gson = new Gson();

    private static final String BASIC_PATH = FileChecker.getDir() + File.separator + "testCase" + File.separator;

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
                .param("testSuiteIdList", "522684bd-d6df-4b47-aab8-b43f1b4c19c0")
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
    public void TestTestCase() throws Exception {
        File file = Resources.getResourceAsFile("testfile/Test.java");
        InputStream csarInputStream = new FileInputStream(file);
        MultipartFile csarMultiFile = new MockMultipartFile(file.getName(), file.getName(),
                ContentType.APPLICATION_OCTET_STREAM.toString(), csarInputStream);
        MvcResult mvcResult = mvc.perform(
                MockMvcRequestBuilders.multipart("/edgegallery/atp/v1/testcases").file("file", csarMultiFile.getBytes())
                        .with(csrf()).param("nameEn", "test").param("nameCh", "test").param("type", "automatic")
                        .param("descriptionCh", "test").param("descriptionEn", "test").param("codeLanguage", "java")
                        .param("expectResultCh", "test").param("expectResultEn", "test").param("testStepEn", "test")
                        .param("testStepCh", "test").param("testSuiteIdList", "522684bd-d6df-4b47-aab8-b43f1b4c19c0"))
                .andReturn();
        int result = mvcResult.getResponse().getStatus();
        assertEquals(200, result);

        String content = mvcResult.getResponse().getContentAsString();
        TestCase testCase = gson.fromJson(content, TestCase.class);
        String id = testCase.getId();

        // java compile
        TestCaseResult resultTestCase = new TestCaseResult();
        testCase.setClassName("Test");

        String filePath = BASIC_PATH + testCase.getNameEn() + Constant.UNDER_LINE + testCase.getId();
        FileChecker.createFile(filePath);
        File targetFile = new File(filePath);
        FileCopyUtils.copy(file, targetFile);
        testCase.setFilePath(filePath);
        TaskTestCase taskTestCase = new TaskTestCase();
        taskTestCase.setId(testCase.getId());
        taskTestCase.setNameCh(testCase.getNameCh());
        taskTestCase.setNameEn(testCase.getNameEn());
        taskTestCase.setReason(Constant.EMPTY);
        taskTestCase.setResult(Constant.RUNNING);
        JavaCompileUtil.executeJava(testCase, "testfile/AR.csar", taskTestCase, null);

        // python call
        new MockUp<PythonInterpreter>() {
            @Mock
            public void initialize(Properties preProperties, Properties postProperties, String[] argv) {}
        };
        File filePython = Resources.getResourceAsFile("testfile/pythonExample.py");
        String filePathPython = BASIC_PATH + "python" + Constant.UNDER_LINE + testCase.getId();
        FileChecker.createFile(filePathPython);
        File targetPythonFile = new File(filePathPython);
        FileCopyUtils.copy(filePython, targetPythonFile);
        Map<String, String> context = new HashMap<String, String>();
        testCase.setFilePath(filePathPython);
        PythonCallUtil.callPython(testCase, "testfile/AR.csar", taskTestCase, context);
        targetPythonFile.delete();

        // jar call
        InputStream stream = getClass().getClassLoader().getResourceAsStream("testfile/Bomb Defense.jar");
        String filePathJar = BASIC_PATH + "jar" + Constant.UNDER_LINE + "111";
        FileChecker.createFile(filePathJar);
        File targetJarFile = new File(filePathJar);
        testCase.setFilePath(filePathJar);
        FileUtils.copyInputStreamToFile(stream, targetJarFile);
        JarCallUtil.executeJar(testCase, "testfile/AR.csar", taskTestCase, context);
        targetJarFile.delete();

        // dowload test case
        MvcResult mvcResultReport = mvc
                .perform(MockMvcRequestBuilders.get("/edgegallery/atp/v1/testcases/" + id + "/action/download")
                        .contentType(MediaType.APPLICATION_JSON_VALUE).with(csrf())
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        int resultReport = mvcResultReport.getResponse().getStatus();
        assertEquals(200, resultReport);

        // delete
        MvcResult mvcResultDelete = mvc
                .perform(MockMvcRequestBuilders.delete("/edgegallery/atp/v1/testcases/" + id).with(csrf())
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        int resultDelete = mvcResultDelete.getResponse().getStatus();
        assertEquals(200, resultDelete);
    }
}
