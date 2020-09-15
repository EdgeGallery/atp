package org.edgegallery.atp.application.testcase.compliance;

import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.edgegallery.atp.application.testcase.TestCase;
import org.edgegallery.atp.config.Constant;
import org.edgegallery.atp.config.ExceptionConstant;
import org.edgegallery.atp.model.TestCaseResult;
import org.edgegallery.atp.utils.TestCaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of validating .mf file must be in root directory.
 */
public class SuffixTestCase extends TestCase {

	private static final Logger LOGGER = LoggerFactory.getLogger(SuffixTestCase.class);

	private TestCaseResult testCaseResult = new TestCaseResult();

	@Override
	public TestCaseResult execute(String filePath) {
		try (ZipFile zipFile = new ZipFile(filePath)) {
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				// root directory and file is end of mf
				if (entry.getName().split("/").length == 2 && TestCaseUtil.fileSuffixValidate("mf", entry.getName())) {
					return setTestCaseResult(Constant.Result.SUCCESS, Constant.EMPTY, testCaseResult);
				}
			}
		} catch (IOException e) {
			LOGGER.error("SuffixTestCase execute failed. {}", e.getMessage());
			return setTestCaseResult(Constant.Result.FAILED, ExceptionConstant.INNER_EXCEPTION, testCaseResult);
		}
		return setTestCaseResult(Constant.Result.FAILED, ExceptionConstant.MFContentTestCase.FILE_NOT_EXIST,
				testCaseResult);
	}

}
