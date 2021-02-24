package org.edgegallery.atp.interfaceTest;

import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import org.apache.http.entity.ContentType;
import org.apache.ibatis.io.Resources;
import org.edgegallery.atp.ATPApplicationTest;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.interfaces.filter.AccessTokenFilter;
import org.edgegallery.atp.model.task.TaskIdList;
import org.edgegallery.atp.model.task.TaskRequest;
import org.edgegallery.atp.utils.CommonUtil;
import org.edgegallery.atp.utils.FileChecker;
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
import mockit.Mock;
import mockit.MockUp;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ATPApplicationTest.class)
@AutoConfigureMockMvc
public class TaskTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private Gson gson = new Gson();

    @Before
    public void setUp() throws Exception {
        Map<String, String> contextMap = new HashMap<String, String>();
        contextMap.put(Constant.ACCESS_TOKEN, "aaa");
        contextMap.put(Constant.USER_ID, "4eed814b-5d29-4e4c-ba73-51fc6db4ed86");
        contextMap.put(Constant.USER_NAME, "atp");
        AccessTokenFilter.context.set(contextMap);
        this.mvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).apply(springSecurity()).build();
    }

    @WithMockUser(roles = "ATP_TENANT")
    @Test
    public void TasksTest() throws Exception {
        new MockUp<FileChecker>() {
            @Mock
            private boolean isAllowedFileName(String originalFilename) {
                return true;
            }
        };
        new MockUp<CommonUtil>() {
            @Mock
            public void dependencyCheckSchdule(String filePath, Stack<Map<String, String>> dependencyStack,
                    Map<String, String> context) {}
        };
        File csar = Resources.getResourceAsFile("testfile/AR.csar");
        InputStream csarInputStream = new FileInputStream(csar);
        MultipartFile csarMultiFile = new MockMultipartFile("AR.csar", "AR.csar",
                ContentType.APPLICATION_OCTET_STREAM.toString(), csarInputStream);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.multipart("/edgegallery/atp/v1/tasks")
                .file("file", csarMultiFile.getBytes()).with(csrf())).andReturn();
        int result = mvcResult.getResponse().getStatus();
        assertEquals(200, result);

        String content = mvcResult.getResponse().getContentAsString();
        TaskRequest task = gson.fromJson(content, TaskRequest.class);
        String id = task.getId();

        // run task
        MvcResult mvcResultRunTasks =
                mvc.perform(MockMvcRequestBuilders.multipart("/edgegallery/atp/v1/tasks/" + id + "/action/run")
                        .with(csrf()).param("scenarioIdList", "e71718a5-864a-49e5-855a-5805a5e9f97d")).andReturn();
        int resultRunTasks = mvcResultRunTasks.getResponse().getStatus();
        assertEquals(200, resultRunTasks);

        // query all tasks
        MvcResult mvcResultQuery = mvc.perform(MockMvcRequestBuilders.get("/edgegallery/atp/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON_VALUE).with(csrf()).accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        int resultQuery = mvcResultQuery.getResponse().getStatus();
        assertEquals(200, resultQuery);

        // query one taks
        MvcResult mvcResultQueryOne = mvc.perform(MockMvcRequestBuilders.get("/edgegallery/atp/v1/tasks/" + id)
                .contentType(MediaType.APPLICATION_JSON_VALUE).with(csrf()).accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        int resultQueryOne = mvcResultQueryOne.getResponse().getStatus();
        assertEquals(200, resultQueryOne);

        // analysis
        MvcResult mvcResultAnalysis =
                mvc.perform(MockMvcRequestBuilders.get("/edgegallery/atp/v1/tasks/action/analysize").with(csrf()))
                        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        int resultAnalysis = mvcResultAnalysis.getResponse().getStatus();
        assertEquals(200, resultAnalysis);

        // batch delete
        TaskIdList list = new TaskIdList();
        List<String> taskIds = new ArrayList<String>();
        taskIds.add(id);
        list.setTaskIds(taskIds);
        MvcResult mvcResultDelete = mvc.perform(
                MockMvcRequestBuilders.post("/edgegallery/atp/v1/tasks/batch_delete").content(gson.toJson(list))
                        .with(csrf()).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        int resultDelete = mvcResultDelete.getResponse().getStatus();
        assertEquals(200, resultDelete);
    }
}
