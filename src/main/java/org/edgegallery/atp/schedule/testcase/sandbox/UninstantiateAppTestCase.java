package org.edgegallery.atp.schedule.testcase.sandbox;

import java.util.Map;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.constant.ExceptionConstant;
import org.edgegallery.atp.model.testcase.TestCaseResult;
import org.edgegallery.atp.schedule.testcase.TestCaseAbs;
import org.edgegallery.atp.utils.CommonUtil;

/**
 * terminate app instance.
 *
 */
public class UninstantiateAppTestCase extends TestCaseAbs {

    private TestCaseResult testCaseResult = new TestCaseResult();

    @Override
    public TestCaseResult execute(String filePath, Map<String, String> context) {
        String appInstanceId = context.get(Constant.APP_INSTANCE_ID);

        return CommonUtil.deleteAppInstance(appInstanceId, context)
                ? setTestCaseResult(Constant.SUCCESS, Constant.EMPTY, testCaseResult)
                : setTestCaseResult(Constant.FAILED, ExceptionConstant.UNINSTANTIATE_APP_FAILED.concat(appInstanceId),
                        testCaseResult);
    }

}
