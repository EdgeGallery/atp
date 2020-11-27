package org.edgegallery.atp.schedule.testcase.sandbox;

import java.util.Map;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.constant.ExceptionConstant;
import org.edgegallery.atp.model.testcase.TestCaseResult;
import org.edgegallery.atp.schedule.testcase.TestCaseAbs;
import org.edgegallery.atp.utils.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * terminate app instance.
 *
 */
public class UninstantiateAppTestCase extends TestCaseAbs {
    private static final Logger LOGGER = LoggerFactory.getLogger(UninstantiateAppTestCase.class);

    private TestCaseResult testCaseResult = new TestCaseResult();

    @Override
    public TestCaseResult execute(String filePath, Map<String, String> context) {
        String appInstanceId = context.get(Constant.APP_INSTANCE_ID);
        // String dependencyInstanceList = context.get(Constant.DEPENDENCY_APP_INSTANCE_ID);
        //
        // List<String> failedInstance = new ArrayList<String>();
        // if (StringUtils.isNotEmpty(dependencyInstanceList)) {
        // String[] dependencyInstanceArray =
        // dependencyInstanceList.substring(dependencyInstanceList.length() - 1).split(Constant.COMMA);
        // Arrays.stream(dependencyInstanceArray).forEach(instance -> {
        // if (!CommonUtil.deleteAppInstance(instance, context)) {
        // failedInstance.add(instance);
        // }
        // });
        // }
        //
        // if (!CollectionUtils.isEmpty(failedInstance)) {
        // LOGGER.error("some instance not terminate successfully.");
        // return setTestCaseResult(Constant.FAILED,
        // ExceptionConstant.UNINSTANTIATE_DEPENDENCE_APP_FAILED.concat(failedInstance.toString()),
        // testCaseResult);
        // }

        return CommonUtil.deleteAppInstance(appInstanceId, context)
                ? setTestCaseResult(Constant.SUCCESS, Constant.EMPTY, testCaseResult)
                : setTestCaseResult(Constant.FAILED, ExceptionConstant.UNINSTANTIATE_APP_FAILED.concat(appInstanceId),
                        testCaseResult);
    }

}
