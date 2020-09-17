package org.edgegallery.atp;

import java.io.IOException;

import org.edgegallery.atp.model.testcase.TestCaseResult;
import org.edgegallery.atp.utils.JSONUtil;
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
		JSONUtil.marshal(result);

		String testCase = "{\"result\":\"success\",\"reason\":\"ok\"}";
		JSONUtil.unMarshal(testCase, TestCaseResult.class);
	}
}
