package org.edgegallery.atp.application.testcase;

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
	public boolean testCaseHandler(String pkgPth, String filePath) {
		try {
			Class<?> clazz = Class.forName(pkgPth);
			return (boolean) clazz.getMethod(METHOD_NAME, String.class).invoke(clazz.newInstance(), filePath);
		} catch (Exception e) {
			LOGGER.error("testCaseHandler failed. {}", e.getMessage());
			return false;
		}
	}

}
