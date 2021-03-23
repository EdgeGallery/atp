package org.edgegallery.atp.util;

import java.io.IOException;
import org.edgegallery.atp.ATPApplicationTest;
import org.edgegallery.atp.model.testcase.TestCaseResult;
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
        TestCaseResult result = new TestCaseResult("sucess", "ok");
        JsonUtil.marshal(result);

        String testCase = "{\"result\":\"success\",\"reason\":\"ok\"}";
        JsonUtil.unMarshal(testCase, TestCaseResult.class);
    }
}
