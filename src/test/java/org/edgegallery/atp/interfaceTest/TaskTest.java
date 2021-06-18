/*
 * Copyright 2020 Huawei Technologies Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.edgegallery.atp.interfaceTest;

import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.entity.ContentType;
import org.apache.ibatis.io.Resources;
import org.edgegallery.atp.ATPApplicationTest;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.interfaces.filter.AccessTokenFilter;
import org.edgegallery.atp.model.ResponseObject;
import org.edgegallery.atp.model.task.IdList;
import org.edgegallery.atp.model.task.TaskRequest;
import org.edgegallery.atp.model.task.TestCaseStatusReq;
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
import org.springframework.web.multipart.MultipartFile;
import mockit.Mock;
import mockit.MockUp;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ATPApplicationTest.class)
@AutoConfigureMockMvc
public class TaskTest {

    @Autowired
    private MockMvc mvc;

    private Gson gson = new Gson();

    @Before
    public void setUp() throws Exception {
        Map<String, String> contextMap = new HashMap<String, String>();
        contextMap.put(Constant.ACCESS_TOKEN, "aaa");
        contextMap.put(Constant.USER_ID, "4eed814b-5d29-4e4c-ba73-51fc6db4ed86");
        contextMap.put(Constant.USER_NAME, "atp");
        AccessTokenFilter.context.set(contextMap);
    }

    @WithMockUser(roles = "ATP_ADMIN")
    @Test
    public void TasksTest() throws Exception {
        new MockUp<FileChecker>() {
            @Mock
            private boolean isAllowedFileName(String originalFilename) {
                return true;
            }
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

        // update test case status
        TestCaseStatusReq req = new TestCaseStatusReq();
        req.setTestScenarioId("4d203111-1111-4f62-aabb-8ebcec357f87");
        req.setTestSuiteId("522684bd-d6df-4b47-aab8-b43f1b4c19c0");
        req.setTestCaseId("4d203173-1111-4f62-aabb-8ebcec357f87");
        req.setResult("success");
        req.setReason("");
        List<TestCaseStatusReq> reqList = new ArrayList<TestCaseStatusReq>();
        reqList.add(req);
        mvc.perform(MockMvcRequestBuilders.put("/edgegallery/atp/v1/tasks/" + id + "/testcase").with(csrf())
                .content(gson.toJson(reqList)).contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)).andExpect(MockMvcResultMatchers.status().isOk());

        // batch delete
        IdList list = new IdList();
        List<String> taskIds = new ArrayList<String>();
        taskIds.add(id);
        list.setIds(taskIds);
        MvcResult mvcResultDelete = mvc.perform(
                MockMvcRequestBuilders.post("/edgegallery/atp/v1/tasks/batch_delete").content(gson.toJson(list))
                        .with(csrf()).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        int resultDelete = mvcResultDelete.getResponse().getStatus();
        assertEquals(200, resultDelete);
    }

    @WithMockUser(roles = "ATP_ADMIN")
    @Test
    public void TasksTestV2() throws Exception {
        new MockUp<FileChecker>() {
            @Mock
            private boolean isAllowedFileName(String originalFilename) {
                return true;
            }
        };

        File csar = Resources.getResourceAsFile("testfile/AR.csar");
        InputStream csarInputStream = new FileInputStream(csar);
        MultipartFile csarMultiFile = new MockMultipartFile("AR.csar", "AR.csar",
                ContentType.APPLICATION_OCTET_STREAM.toString(), csarInputStream);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.multipart("/edgegallery/atp/v2/tasks")
                .file("file", csarMultiFile.getBytes()).with(csrf())).andReturn();
        int result = mvcResult.getResponse().getStatus();
        assertEquals(200, result);

        String content = mvcResult.getResponse().getContentAsString();
        ResponseObject responseObject = JSONObject.parseObject(content, ResponseObject.class);
        String data = JSONObject.toJSONString(responseObject.getData());
        TaskRequest task = JSONObject.parseObject(data, TaskRequest.class);
        String id = task.getId();

        // run task
        MvcResult mvcResultRunTasks =
                mvc.perform(MockMvcRequestBuilders.multipart("/edgegallery/atp/v2/tasks/" + id + "/action/run")
                        .with(csrf()).param("scenarioIdList", "e71718a5-864a-49e5-855a-5805a5e9f97d")).andReturn();
        int resultRunTasks = mvcResultRunTasks.getResponse().getStatus();
        assertEquals(200, resultRunTasks);

        // query one taks
        MvcResult mvcResultQueryOne = mvc.perform(MockMvcRequestBuilders.get("/edgegallery/atp/v2/tasks/" + id)
                .contentType(MediaType.APPLICATION_JSON_VALUE).with(csrf()).accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        int resultQueryOne = mvcResultQueryOne.getResponse().getStatus();
        assertEquals(200, resultQueryOne);
    }

    @WithMockUser(roles = "ATP_ADMIN")
    @Test
    public void getTaskByIdFileNotExistsV2() throws Exception {
        MvcResult mvcResultQueryOne = mvc
                .perform(MockMvcRequestBuilders.get("/edgegallery/atp/v2/tasks/33333111-1111-4f62-aabb-8ebcec357f87")
                .contentType(MediaType.APPLICATION_JSON_VALUE).with(csrf()).accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        int resultQueryOne = mvcResultQueryOne.getResponse().getStatus();
        assertEquals(404, resultQueryOne);
    }

    @WithMockUser(roles = "ATP_ADMIN")
    @Test
    public void getTaskByIdFileNotExists() throws Exception {
        MvcResult mvcResultQueryOne = mvc
                .perform(MockMvcRequestBuilders.get("/edgegallery/atp/v1/tasks/33333111-1111-4f62-aabb-8ebcec357f87")
                        .contentType(MediaType.APPLICATION_JSON_VALUE).with(csrf())
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        int resultQueryOne = mvcResultQueryOne.getResponse().getStatus();
        assertEquals(404, resultQueryOne);
    }

    @WithMockUser(roles = "ATP_ADMIN")
    @Test
    public void runTaskIllegalRequestionExceptionV2() throws Exception {
        // run task
        MvcResult mvcResultRunTasks = mvc.perform(MockMvcRequestBuilders
                .multipart("/edgegallery/atp/v2/tasks/33333111-1111-4f62-aabb-8ebcec357f87/action/run").with(csrf())
                .param("scenarioIdList", "e71718a5-864a-49e5-855a-5805a5e9f97d")).andReturn();
        int resultRunTasks = mvcResultRunTasks.getResponse().getStatus();
        assertEquals(400, resultRunTasks);
    }

    @WithMockUser(roles = "ATP_ADMIN")
    @Test
    public void runTaskIllegalRequestionException() throws Exception {
        // run task
        MvcResult mvcResultRunTasks = mvc.perform(MockMvcRequestBuilders
                .multipart("/edgegallery/atp/v1/tasks/33333111-1111-4f62-aabb-8ebcec357f87/action/run").with(csrf())
                .param("scenarioIdList", "e71718a5-864a-49e5-855a-5805a5e9f97d")).andReturn();
        int resultRunTasks = mvcResultRunTasks.getResponse().getStatus();
        assertEquals(400, resultRunTasks);
    }
}
