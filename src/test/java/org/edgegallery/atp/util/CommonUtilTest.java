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

package org.edgegallery.atp.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.http.entity.ContentType;
import org.apache.ibatis.io.Resources;
import org.edgegallery.atp.ATPApplicationTest;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.interfaces.filter.AccessTokenFilter;
import org.edgegallery.atp.utils.CommonUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ATPApplicationTest.class)
@AutoConfigureMockMvc
public class CommonUtilTest {
    @Before
    public void setUp() throws Exception {
        Map<String, String> contextMap = new HashMap<String, String>();
        contextMap.put(Constant.ACCESS_TOKEN, "aaa");
        contextMap.put(Constant.USER_ID, "4eed814b-5d29-4e4c-ba73-51fc6db4ed86");
        contextMap.put(Constant.USER_NAME, "atp");
        AccessTokenFilter.CONTEXT.set(contextMap);
    }

    @Test
    public void testCommonUtil() throws IOException {
        CommonUtil.getFormatDate();
        
        // deleteTempFile
        File csar = Resources.getResourceAsFile("testfile/AR.csar");
        InputStream csarInputStream = new FileInputStream(csar);
        MultipartFile csarMultiFile = new MockMultipartFile("AR.csar", "AR.csar",
                ContentType.APPLICATION_OCTET_STREAM.toString(), csarInputStream);
        CommonUtil.deleteTempFile(UUID.randomUUID().toString(), csarMultiFile);

        // UUID exception
        try {
            CommonUtil.isUuidPattern("111");
        } catch (IllegalArgumentException e) {

        }
    }

}
