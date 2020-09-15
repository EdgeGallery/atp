package org.edgegallery.atp.application.testcase;

import org.edgegallery.atp.model.TestCaseResult;

public abstract class TestCase {

	public abstract TestCaseResult execute(String filePath);

	public TestCaseResult setTestCaseResult(String result, String reason, TestCaseResult testCaseresult) {
		testCaseresult.setReason(reason);
		testCaseresult.setResult(result);
		return testCaseresult;
	}

}
