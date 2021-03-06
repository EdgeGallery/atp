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

import java.io.IOException;
import org.edgegallery.atp.ATPApplicationTest;
import org.edgegallery.atp.model.user.User;
import org.edgegallery.atp.utils.JsonUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ATPApplicationTest.class)
public class JSONUtilTest {
    @Test
    public void marshalUnmarshalTest() throws IOException {
        User result = new User("userid", "username");
        JsonUtil.marshal(result);

        String testCase = "{\"userId\":\"userId\",\"userName\":\"userName\"}";
        JsonUtil.unMarshal(testCase, User.class);
    }
}
