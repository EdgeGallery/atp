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
import org.edgegallery.atp.ATPApplicationTest;
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

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ATPApplicationTest.class)
@AutoConfigureMockMvc
public class TestSuiteTestV2 {
    @Autowired
    private MockMvc mvc;

    private Gson gson = new Gson();

    @WithMockUser(roles = "ATP_ADMIN")
    @Test
    public void createTestSuiteTest() throws Exception {
        // create test suite
        MvcResult mvcResult = mvc.perform(
            MockMvcRequestBuilders.multipart("/edgegallery/atp/v2/testsuites").with(csrf()).param("nameEn", "testSuite")
                .param("nameCh", "").param("descriptionCh", "testSuite").param("descriptionEn", "")
                .param("scenarioIdList", "4d203111-1111-4f62-aabb-8ebcec357f87")).andReturn();
        int result = mvcResult.getResponse().getStatus();
        assertEquals(200, result);
    }

    @WithMockUser(roles = "ATP_ADMIN")
    @Test
    public void getOneTestSuiteTest() throws Exception {
        MvcResult mvcResultQueryOne = mvc.perform(
            MockMvcRequestBuilders.get("/edgegallery/atp/v2/testsuites/333684bd-d6df-4b47-aab8-b43f1b4c19c0")
                .contentType(MediaType.APPLICATION_JSON_VALUE).with(csrf()).accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        int resultQueryOne = mvcResultQueryOne.getResponse().getStatus();
        assertEquals(200, resultQueryOne);
    }

    @WithMockUser(roles = "ATP_ADMIN")
    @Test
    public void getAllTestSuitesTest() throws Exception {
        MvcResult mvcResultQueryAll = mvc.perform(
            MockMvcRequestBuilders.get("/edgegallery/atp/v2/testsuites?limit=10&offset=0")
                .contentType(MediaType.APPLICATION_JSON_VALUE).with(csrf()).accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();
        int resultQueryAll = mvcResultQueryAll.getResponse().getStatus();
        assertEquals(200, resultQueryAll);
    }

    @WithMockUser(roles = "ATP_ADMIN")
    @Test
    public void getAllTestSuitesFilterTest() throws Exception {
        MvcResult mvcResultFilterQueryAll = mvc.perform(MockMvcRequestBuilders
            .get("/edgegallery/atp/v2/testsuites?limit=10&offset=0&scenarioIdList=4d203111-1111-4f62-aabb-8ebcec357f87")
            .contentType(MediaType.APPLICATION_JSON_VALUE).with(csrf()).accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();
        int resultFilterQueryAll = mvcResultFilterQueryAll.getResponse().getStatus();
        assertEquals(200, resultFilterQueryAll);
    }

    @WithMockUser(roles = "ATP_ADMIN")
    @Test
    public void deleteTestSuitesExceptionTest() throws Exception {
        MvcResult mvcResultDelete = mvc.perform(
            MockMvcRequestBuilders.delete("/edgegallery/atp/v2/testsuites/333684bd-d6df-4b47-aab8-b43f1b4c19c0")
                .with(csrf()).accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
        int resultDelete = mvcResultDelete.getResponse().getStatus();
        assertEquals(400, resultDelete);
    }
}
