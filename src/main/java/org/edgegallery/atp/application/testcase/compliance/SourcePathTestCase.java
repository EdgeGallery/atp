package org.edgegallery.atp.application.testcase.compliance;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
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
 * Implementation of validating sourcePath availability.
 */
public class SourcePathTestCase extends TestCase {

	private static final Logger LOGGER = LoggerFactory.getLogger(SourcePathTestCase.class);

	private static Set<String> pathSet = new HashSet<String>();

	private TestCaseResult testCaseResult = new TestCaseResult();

	@Override
	public TestCaseResult execute(String filePath) {
		Set<String> sourcePathSet = new HashSet<String>();
		try (ZipFile zipFile = new ZipFile(filePath)) {
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				String entryName = entry.getName();

				String path = entryName.substring(entryName.indexOf("/") + 1).trim();
				pathSet.add(TestCaseUtil.removeLastSlash(path));

				// root directory and file is end of mf
				if (entry.getName().split("/").length == 2 && TestCaseUtil.fileSuffixValidate("mf", entry.getName())) {
					Set<String> prefix = new HashSet<String>() {
						{
							add("Source");
						}
					};
					sourcePathSet = TestCaseUtil.getPathSet(zipFile, entry, prefix);
				}
			}
		} catch (IOException e) {
			LOGGER.error("SourcePathTestCase execute failed. {}", e.getMessage());
			return setTestCaseResult(Constant.Result.FAILED, ExceptionConstant.INNER_EXCEPTION, testCaseResult);
		}
		return pathSet.containsAll(sourcePathSet)?setTestCaseResult(Constant.Result.SUCCESS, Constant.EMPTY, testCaseResult)
				: setTestCaseResult(Constant.Result.FAILED, ExceptionConstant.SourcePathTestCase.SOURCE_PATH_FILE_NOT_EXISTS,
						testCaseResult);
	}

}
