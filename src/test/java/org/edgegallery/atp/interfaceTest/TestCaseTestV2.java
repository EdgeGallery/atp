/*
 * Copyright 2021 Huawei Technologies Co., Ltd.
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
import org.apache.http.entity.ContentType;
import org.apache.ibatis.io.Resources;
import org.edgegallery.atp.ATPApplicationTest;
import org.edgegallery.atp.model.ResponseObject;
import org.edgegallery.atp.model.testcase.TestCase;
import org.edgegallery.atp.utils.FileChecker;
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

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ATPApplicationTest.class)
@AutoConfigureMockMvc
public class TestCaseTestV2 {

    @Autowired
    private MockMvc mvc;

    private Gson gson = new Gson();

    private static final String BASIC_PATH = FileChecker.getDir() + File.separator + "testCase" + File.separator;

    @WithMockUser(roles = "ATP_TENANT")
    @Test
    public void getAllTestCases() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
            .get("/edgegallery/atp/v2/testcases?limit=10&offset=0&locale='en'&name='Virus Scanning'")
            .contentType(MediaType.APPLICATION_JSON_VALUE).with(csrf()).accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        int result = mvcResult.getResponse().getStatus();
        assertEquals(200, result);
    }

    @WithMockUser(roles = "ATP_TENANT")
    @Test
    public void getAllTestCaesWithParameters() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
            .get("/edgegallery/atp/v2/testcases?limit=10&offset=0&locale='en'&name='Virus Scanning'")
            .param("testSuiteIdList", "522684bd-d6df-4b47-aab8-b43f1b4c19c0")
            .param("configIdList", "4353982a-abb0-4cf6-9cae-a468a5318c67'")
            .contentType(MediaType.APPLICATION_JSON_VALUE).with(csrf()).accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        int result = mvcResult.getResponse().getStatus();
        assertEquals(200, result);
    }

    @WithMockUser(roles = "ATP_TENANT")
    @Test
    public void getOneTestCaesWithParameters() throws Exception {
        MvcResult mvcResult = mvc.perform(
            MockMvcRequestBuilders.get("/edgegallery/atp/v2/testcases/4d203173-6666-4f62-aabb-8ebcec357f87")
                .with(csrf()).accept(MediaType.APPLICATION_JSON_VALUE)).andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();
        int result = mvcResult.getResponse().getStatus();
        assertEquals(200, result);
    }

    @WithMockUser(roles = "ATP_ADMIN")
    @Test
    public void createTestCase() throws Exception {
        File file = Resources.getResourceAsFile("testfile/Test.java");
        InputStream csarInputStream = new FileInputStream(file);
        MultipartFile csarMultiFile = new MockMultipartFile(file.getName(), file.getName(),
            ContentType.APPLICATION_OCTET_STREAM.toString(), csarInputStream);
        MvcResult mvcResult = mvc.perform(
            MockMvcRequestBuilders.multipart("/edgegallery/atp/v2/testcases").file("file", csarMultiFile.getBytes())
                .with(csrf()).param("nameEn", "test").param("nameCh", "").param("type", "automatic")
                .param("descriptionCh", "test").param("descriptionEn", "").param("codeLanguage", "java")
                .param("expectResultCh", "test").param("expectResultEn", "").param("testStepEn", "test")
                .param("testStepCh", "").param("testSuiteIdList", "522684bd-d6df-4b47-aab8-b43f1b4c19c0")
                .param("configIdList", "")).andReturn();
        int result = mvcResult.getResponse().getStatus();
        assertEquals(200, result);

        String content = mvcResult.getResponse().getContentAsString();
        ResponseObject responseObject = JSONObject.parseObject(content, ResponseObject.class);
        String data = JSONObject.toJSONString(responseObject.getData());
        TestCase task = JSONObject.parseObject(data, TestCase.class);
        String id = task.getId();

        MvcResult mvcResultReport = mvc.perform(
            MockMvcRequestBuilders.get("/edgegallery/atp/v2/testcases/" + id + "/action/download")
                .contentType(MediaType.APPLICATION_JSON_VALUE).with(csrf()).accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        int resultReport = mvcResultReport.getResponse().getStatus();
        assertEquals(200, resultReport);
    }

    @WithMockUser(roles = "ATP_ADMIN")
    @Test
    public void deleteTestCase() throws Exception {
        MvcResult mvcResultDelete = mvc.perform(
            MockMvcRequestBuilders.delete("/edgegallery/atp/v2/testcases/4d203173-2222-4f62-aabb-8ebcec357f87")
                .with(csrf()).accept(MediaType.APPLICATION_JSON_VALUE)).andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();
        int resultDelete = mvcResultDelete.getResponse().getStatus();
        assertEquals(200, resultDelete);
    }
}
