package org.edgegallery.atp.schedule.testcase;

import java.util.Map;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.constant.ExceptionConstant;
import org.edgegallery.atp.model.testcase.TestCaseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * execute test case dynamically
 *
 */
public class TestCaseHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestCaseHandler.class);

    private static final String METHOD_NAME = "execute";

    private static TestCaseHandler instance = new TestCaseHandler();

    public static TestCaseHandler getInstantce() {
        return instance;
    }

    /**
     * dynamic schedule test case
     * 
     * @param pkgPth org.edgegallery.atp.schedule.testcase.compliance.SuffixTestCase
     * @param filePath filePath
     * @param context context info
     * @return
     */
    public TestCaseResult testCaseHandler(String pkgPth, String filePath, Map<String, String> context) {
        try {
            Class<?> clazz = Class.forName(pkgPth);
            return (TestCaseResult) clazz.getMethod(METHOD_NAME, String.class, Map.class).invoke(clazz.newInstance(),
                    filePath, context);
        } catch (Exception e) {
            LOGGER.error("testCaseHandler failed. {}", e.getMessage());
            return new TestCaseResult(Constant.Status.FAILED, ExceptionConstant.INNER_EXCEPTION);
        }
    }

}
