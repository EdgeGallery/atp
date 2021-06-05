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
import org.edgegallery.atp.model.testscenario.TestScenario;
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
public class TestScenarioTest {
    @Autowired
    private MockMvc mvc;

    private Gson gson = new Gson();

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
                .get("/edgegallery/atp/v1/files/" + id + "?type =scenario").with(csrf())).andReturn();
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

    @WithMockUser(roles = "ATP_ADMIN")
    @Test
    public void testModelImportTest() throws Exception {
        File file = Resources.getResourceAsFile("testfile/batch_import.zip");
        InputStream zipInputStream = new FileInputStream(file);
        MultipartFile zipMultiFile = new MockMultipartFile("batch_import.zip", "batch_import.zip",
                ContentType.APPLICATION_OCTET_STREAM.toString(), zipInputStream);
        MvcResult mvcResult =
                mvc.perform(MockMvcRequestBuilders.multipart("/edgegallery/atp/v1/testmodels/action/import")
                        .file("file", zipMultiFile.getBytes()).with(csrf())).andReturn();
        int result = mvcResult.getResponse().getStatus();
        assertEquals(206, result);
    }
}
