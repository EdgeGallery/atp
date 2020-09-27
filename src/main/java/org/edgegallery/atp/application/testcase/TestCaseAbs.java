package org.edgegallery.atp.application.testcase;

import java.util.Map;
import org.edgegallery.atp.model.testcase.TestCaseResult;

public abstract class TestCaseAbs {

    public abstract TestCaseResult execute(String filePath, Map<String, String> context);

    public TestCaseResult setTestCaseResult(String result, String reason, TestCaseResult testCaseresult) {
        testCaseresult.setReason(reason);
        testCaseresult.setResult(result);
        return testCaseresult;
    }

}
