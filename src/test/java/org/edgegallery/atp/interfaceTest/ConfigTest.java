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
import org.edgegallery.atp.ATPApplicationTest;
import org.edgegallery.atp.model.ResponseObject;
import org.edgegallery.atp.model.config.Config;
import org.edgegallery.atp.model.config.ConfigBase;
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

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ATPApplicationTest.class)
@AutoConfigureMockMvc
public class ConfigTest {
    @Autowired
    private MockMvc mvc;

    private Gson gson = new Gson();

    @WithMockUser(roles = "ATP_ADMIN")
    @Test
    public void createConfigTest() throws Exception {
        ConfigBase config = new ConfigBase();
        config.setDescriptionEn("test");
        config.setConfiguration("a=b;");
        config.setNameEn("test");
        MvcResult mvcResult = mvc.perform(
            MockMvcRequestBuilders.post("/edgegallery/atp/v2/configs").content(gson.toJson(config)).with(csrf())
                .contentType(MediaType.APPLICATION_JSON_UTF8).accept(MediaType.APPLICATION_JSON_UTF8)).andReturn();
        int result = mvcResult.getResponse().getStatus();
        assertEquals(200, result);

        String content = mvcResult.getResponse().getContentAsString();
        ResponseObject responseObject = JSONObject.parseObject(content, ResponseObject.class);
        String data = JSONObject.toJSONString(responseObject.getData());
        Config resultConfig = JSONObject.parseObject(data, Config.class);
        String id = resultConfig.getId();

        //delete config
        MvcResult mvcResultDelete = mvc.perform(
            MockMvcRequestBuilders.delete("/edgegallery/atp/v2/configs/" + id).with(csrf())
                .contentType(MediaType.APPLICATION_JSON_UTF8).with(csrf()).accept(MediaType.APPLICATION_JSON_UTF8))
            .andReturn();
        int resultDelete = mvcResultDelete.getResponse().getStatus();
        assertEquals(200, resultDelete);
    }

    @WithMockUser(roles = "ATP_ADMIN")
    @Test
    public void createConfigConfigurationNullTest() throws Exception {
        ConfigBase config = new ConfigBase();
        config.setConfiguration("a=b;");
        MvcResult mvcResult = mvc.perform(
            MockMvcRequestBuilders.post("/edgegallery/atp/v2/configs").content(gson.toJson(config)).with(csrf())
                .contentType(MediaType.APPLICATION_JSON_UTF8).accept(MediaType.APPLICATION_JSON_UTF8)).andReturn();
        int result = mvcResult.getResponse().getStatus();
        assertEquals(400, result);
    }

    @WithMockUser(roles = "ATP_ADMIN")
    @Test
    public void createConfigNameNullTest() throws Exception {
        ConfigBase config = new ConfigBase();
        config.setDescriptionEn("test");
        config.setConfiguration("a=b;");
        MvcResult mvcResult = mvc.perform(
            MockMvcRequestBuilders.post("/edgegallery/atp/v2/configs").content(gson.toJson(config)).with(csrf())
                .contentType(MediaType.APPLICATION_JSON_UTF8).accept(MediaType.APPLICATION_JSON_UTF8)).andReturn();
        int result = mvcResult.getResponse().getStatus();
        assertEquals(400, result);
    }

    @WithMockUser(roles = "ATP_ADMIN")
    @Test
    public void createConfigPatternErrorTest2() throws Exception {
        ConfigBase config = new ConfigBase();
        config.setDescriptionEn("test");
        config.setConfiguration("abv");
        config.setNameEn("test2");
        MvcResult mvcResult = mvc.perform(
            MockMvcRequestBuilders.post("/edgegallery/atp/v2/configs").content(gson.toJson(config)).with(csrf())
                .contentType(MediaType.APPLICATION_JSON_UTF8).accept(MediaType.APPLICATION_JSON_UTF8)).andReturn();
        int result = mvcResult.getResponse().getStatus();
        assertEquals(400, result);
    }

    @WithMockUser(roles = "ATP_ADMIN")
    @Test
    public void createConfigPatternErrorTest() throws Exception {
        ConfigBase config = new ConfigBase();
        config.setDescriptionEn("test");
        config.setConfiguration("");
        config.setNameEn("test1");
        MvcResult mvcResult = mvc.perform(
            MockMvcRequestBuilders.post("/edgegallery/atp/v2/configs").content(gson.toJson(config)).with(csrf())
                .contentType(MediaType.APPLICATION_JSON_UTF8).accept(MediaType.APPLICATION_JSON_UTF8)).andReturn();
        int result = mvcResult.getResponse().getStatus();
        assertEquals(400, result);
    }

    @WithMockUser(roles = "ATP_ADMIN")
    @Test
    public void updateConfig() throws Exception {
        ConfigBase config = new ConfigBase();
        config.setDescriptionCh("modify");
        MvcResult mvcResult = mvc.perform(
            MockMvcRequestBuilders.put("/edgegallery/atp/v2/configs/4353982a-abb0-4cf6-9cae-a468a5318c67").with(csrf())
                .content(gson.toJson(config)).contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)).andReturn();
        int result = mvcResult.getResponse().getStatus();
        assertEquals(200, result);
    }

    @WithMockUser(roles = "ATP_ADMIN")
    @Test
    public void updateConfigIdNotExists() throws Exception {
        ConfigBase config = new ConfigBase();
        config.setDescriptionCh("modify");
        MvcResult mvcResult = mvc.perform(
            MockMvcRequestBuilders.put("/edgegallery/atp/v2/configs/4353982a-1111-4cf6-9cae-a468a5318c67").with(csrf())
                .content(gson.toJson(config)).contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)).andReturn();
        int result = mvcResult.getResponse().getStatus();
        assertEquals(404, result);
    }

    @WithMockUser(roles = "ATP_ADMIN")
    @Test
    public void queryConfig() throws Exception {
        MvcResult mvcResult = mvc.perform(
            MockMvcRequestBuilders.get("/edgegallery/atp/v2/configs/4353982a-abb0-4cf6-9cae-a468a5318c67").with(csrf())
                .contentType(MediaType.APPLICATION_JSON_UTF8).with(csrf()).accept(MediaType.APPLICATION_JSON_UTF8))
            .andReturn();
        int result = mvcResult.getResponse().getStatus();
        assertEquals(200, result);
    }

    @WithMockUser(roles = "ATP_ADMIN")
    @Test
    public void queryConfigIdNotExists() throws Exception {
        MvcResult mvcResult = mvc.perform(
            MockMvcRequestBuilders.get("/edgegallery/atp/v2/configs/4353982a-1111-4cf6-9cae-a468a5318c67").with(csrf())
                .contentType(MediaType.APPLICATION_JSON_UTF8).with(csrf()).accept(MediaType.APPLICATION_JSON_UTF8))
            .andReturn();
        int result = mvcResult.getResponse().getStatus();
        assertEquals(404, result);
    }

    @WithMockUser(roles = "ATP_ADMIN")
    @Test
    public void queryAllConfigs() throws Exception {
        MvcResult mvcResult = mvc.perform(
            MockMvcRequestBuilders.get("/edgegallery/atp/v2/configs?limit=10&offset=0").with(csrf())
                .contentType(MediaType.APPLICATION_JSON_UTF8).with(csrf()).accept(MediaType.APPLICATION_JSON_UTF8))
            .andReturn();
        int result = mvcResult.getResponse().getStatus();
        assertEquals(200, result);
    }

    @WithMockUser(roles = "ATP_ADMIN")
    @Test
    public void deleteConfigException() throws Exception {
        MvcResult mvcResult = mvc.perform(
            MockMvcRequestBuilders.delete("/edgegallery/atp/v2/configs/4353982a-abb0-4cf6-9cae-a468a5318c67")
                .with(csrf()).contentType(MediaType.APPLICATION_JSON_UTF8).with(csrf())
                .accept(MediaType.APPLICATION_JSON_UTF8)).andReturn();
        int result = mvcResult.getResponse().getStatus();
        assertEquals(400, result);
    }
}
