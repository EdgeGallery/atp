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


import com.google.gson.Gson;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import org.apache.http.entity.ContentType;
import org.apache.ibatis.io.Resources;
import org.edgegallery.atp.ATPApplicationTest;
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
public class TestScenarioTestV2 {
    @Autowired
    private MockMvc mvc;

    private Gson gson = new Gson();

    @WithMockUser(roles = "ATP_ADMIN")
    @Test
    public void createScenarioTest() throws Exception {
        // create scenario
        File file = Resources.getResourceAsFile("testfile/icon.png");
        InputStream iconInputStream = new FileInputStream(file);
        MultipartFile iconMultiFile = new MockMultipartFile(file.getName(), file.getName(),
            ContentType.APPLICATION_OCTET_STREAM.toString(), iconInputStream);
        MvcResult mvcResult = mvc.perform(
            MockMvcRequestBuilders.multipart("/edgegallery/atp/v2/testscenarios").file("icon", iconMultiFile.getBytes())
                .with(csrf()).param("nameEn", "testScenario").param("nameCh", "").param("descriptionCh", "testScenario")
                .param("descriptionEn", "").param("label", "EdgeGallery")).andReturn();
        int result = mvcResult.getResponse().getStatus();
        assertEquals(200, result);
    }

    @WithMockUser(roles = "ATP_ADMIN")
    @Test
    public void getIconFileTestNotFound() throws Exception {
        MvcResult mvcResultIconFile = mvc.perform(
            MockMvcRequestBuilders.get("/edgegallery/atp/v2/files/96a82e85-d40d-4ce5-beec-2dd1c9a3d41d?type =scenario")
                .with(csrf())).andReturn();
        int resultIconFile = mvcResultIconFile.getResponse().getStatus();
        assertEquals(404, resultIconFile);
    }

    @WithMockUser(roles = "ATP_ADMIN")
    @Test
    public void getTestCaseTest() throws Exception {
        // get all test case under one test scenario
        MvcResult mvcResultQueryTestCases = mvc.perform(
            MockMvcRequestBuilders.multipart("/edgegallery/atp/v2/testscenarios/testcases").with(csrf())
                .param("scenarioIds", "4d203111-1111-4f62-aabb-8ebcec357f87")).andReturn();
        int resultQueryTestCases = mvcResultQueryTestCases.getResponse().getStatus();
        assertEquals(200, resultQueryTestCases);
    }

    @WithMockUser(roles = "ATP_ADMIN")
    @Test
    public void getOneTestScenarioTest() throws Exception {
        // get one test scenario
        MvcResult mvcResultQueryOne = mvc.perform(
            MockMvcRequestBuilders.get("/edgegallery/atp/v2/testscenarios/4d203111-1111-4f62-aabb-8ebcec357f87")
                .contentType(MediaType.APPLICATION_JSON_VALUE).with(csrf()).accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();
        int resultQueryOne = mvcResultQueryOne.getResponse().getStatus();
        assertEquals(200, resultQueryOne);
    }

    @WithMockUser(roles = "ATP_ADMIN")
    @Test
    public void getAllTestScenariosTest() throws Exception {
        MvcResult mvcResultQueryAll = mvc.perform(
            MockMvcRequestBuilders.get("/edgegallery/atp/v2/testscenarios?limit=10&offset=0")
                .contentType(MediaType.APPLICATION_JSON_VALUE).with(csrf()).accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        int resultQueryAll = mvcResultQueryAll.getResponse().getStatus();
        assertEquals(200, resultQueryAll);
    }

    @WithMockUser(roles = "ATP_ADMIN")
    @Test
    public void deleteTestScenariosTestException() throws Exception {
        // delete
        MvcResult mvcResultDelete = mvc.perform(
            MockMvcRequestBuilders.delete("/edgegallery/atp/v2/testscenarios/4d203111-1111-4f62-aabb-8ebcec357f87")
                .with(csrf()).accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
        int resultDelete = mvcResultDelete.getResponse().getStatus();
        assertEquals(400, resultDelete);
    }

    @WithMockUser(roles = "ATP_ADMIN")
    @Test
    public void testModelImportTest() throws Exception {
        File file = Resources.getResourceAsFile("testfile/batch_import.zip");
        InputStream zipInputStream = new FileInputStream(file);
        MultipartFile zipMultiFile = new MockMultipartFile("batch_import.zip", "batch_import.zip",
            ContentType.APPLICATION_OCTET_STREAM.toString(), zipInputStream);
        MvcResult mvcResult = mvc.perform(
            MockMvcRequestBuilders.multipart("/edgegallery/atp/v2/testmodels/action/import")
                .file("file", zipMultiFile.getBytes()).with(csrf())).andReturn();
        int result = mvcResult.getResponse().getStatus();
        assertEquals(206, result);
    }
}
