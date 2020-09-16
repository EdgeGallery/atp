package org.edgegallery.atp.application.testcase;

import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.constant.ExceptionConstant;
import org.edgegallery.atp.model.testcase.TestCaseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestCaseHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(TestCaseHandler.class);

	private static final String METHOD_NAME = "execute";

	/**
	 * 
	 * @param pkgPth   org.appstore.mec.domain.model.testcase.draft.SuffixTestCase
	 * @param filePath filePath
	 * @return
	 */
	public TestCaseResult testCaseHandler(String pkgPth, String filePath) {
		try {
			Class<?> clazz = Class.forName(pkgPth);
			return (TestCaseResult) clazz.getMethod(METHOD_NAME, String.class).invoke(clazz.newInstance(), filePath);
		} catch (Exception e) {
			LOGGER.error("testCaseHandler failed. {}", e.getMessage());
			return new TestCaseResult(Constant.Result.FAILED, ExceptionConstant.INNER_EXCEPTION);
		}
	}

}
