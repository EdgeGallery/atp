package org.edgegallery.atp.schedule.testcase.compliance;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.constant.ExceptionConstant;
import org.edgegallery.atp.model.testcase.TestCaseResult;
import org.edgegallery.atp.schedule.testcase.TestCaseAbs;
import org.edgegallery.atp.utils.TestCaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of validating .mf file must be in root directory.
 */
public class SuffixTestCase extends TestCaseAbs {

    private static final Logger LOGGER = LoggerFactory.getLogger(SuffixTestCase.class);

    private TestCaseResult testCaseResult = new TestCaseResult();

    @Override
    public TestCaseResult execute(String filePath, Map<String, String> context) {
        try (ZipFile zipFile = new ZipFile(filePath)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                // root directory and file is end of mf
                if (entry.getName().split(Constant.SLASH).length == 2
                        && TestCaseUtil.fileSuffixValidate("mf", entry.getName())) {
                    return setTestCaseResult(Constant.Status.SUCCESS, Constant.EMPTY, testCaseResult);
                }
            }
        } catch (IOException e) {
            LOGGER.error("SuffixTestCase execute failed. {}", e.getMessage());
            return setTestCaseResult(Constant.Status.FAILED, ExceptionConstant.INNER_EXCEPTION, testCaseResult);
        }
        return setTestCaseResult(Constant.Status.FAILED, ExceptionConstant.MFContentTestCase.FILE_NOT_EXIST,
                testCaseResult);
    }

}
