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
import com.google.gson.Gson;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.ibatis.io.Resources;
import org.edgegallery.atp.ATPApplicationTest;
import org.edgegallery.atp.model.contribution.Contribution;
import org.edgegallery.atp.model.task.IdList;
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

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ATPApplicationTest.class)
@AutoConfigureMockMvc
public class ContributionTest {
    @Autowired
    private MockMvc mvc;

    Gson gson = new Gson();

    @WithMockUser(roles = "ATP_ADMIN")
    @Test
    public void testContributionTest() throws Exception {
        // create contribution
        File csar = Resources.getResourceAsFile("testfile/AR.csar");
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.multipart("/edgegallery/atp/v1/contributions")
                .file(new MockMultipartFile("file", "contribution.zip", MediaType.TEXT_PLAIN_VALUE,
                        FileUtils.openInputStream(csar)))
                .with(csrf()).param("name", "test")
                .param("objective", "test")
                .param("step", "automatic").param("expectResult", "test").param("type", "script")).andReturn();
        int result = mvcResult.getResponse().getStatus();
        assertEquals(200, result);
        
        String content = mvcResult.getResponse().getContentAsString();
        Contribution contribution = gson.fromJson(content, Contribution.class);
        String id = contribution.getId();

        // get all contributions
        MvcResult mvcResultQueryAll = mvc.perform(MockMvcRequestBuilders.get("/edgegallery/atp/v1/contributions")
                .contentType(MediaType.APPLICATION_JSON_VALUE).with(csrf()).accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        int resultQueryAll = mvcResultQueryAll.getResponse().getStatus();
        assertEquals(200, resultQueryAll);

        //download contribution
        MvcResult mvcResultDownload = mvc
                .perform(MockMvcRequestBuilders.get("/edgegallery/atp/v1/contributions/" + id + "/action/download")
                .contentType(MediaType.APPLICATION_JSON_VALUE).with(csrf()).accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        int resultDownload = mvcResultDownload.getResponse().getStatus();
        assertEquals(200, resultDownload);

        // batch delete contributions
        IdList list = new IdList();
        List<String> ids = new ArrayList<String>();
        ids.add(id);
        list.setIds(ids);
        MvcResult mvcResultDelete = mvc
                .perform(MockMvcRequestBuilders.post("/edgegallery/atp/v1/contributions/batch_delete")
                        .content(gson.toJson(list)).with(csrf()).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        int resultDelete = mvcResultDelete.getResponse().getStatus();
        assertEquals(200, resultDelete);
    }
}
