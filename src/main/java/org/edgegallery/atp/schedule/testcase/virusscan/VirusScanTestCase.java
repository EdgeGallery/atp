package org.edgegallery.atp.schedule.testcase.virusscan;

import java.util.Map;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.model.testcase.TestCaseResult;
import org.edgegallery.atp.schedule.testcase.TestCaseAbs;

/**
 * execute virus scan class.
 *
 */
public class VirusScanTestCase extends TestCaseAbs {

    private TestCaseResult testCaseResult = new TestCaseResult();

    @Override
    public TestCaseResult execute(String filePath, Map<String, String> context) {

        return setTestCaseResult(Constant.SUCCESS, Constant.EMPTY, testCaseResult);
    }

}
