package org.edgegallery.atp.application.testcase.compliance;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.edgegallery.atp.application.testcase.TestCaseAbs;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.constant.ExceptionConstant;
import org.edgegallery.atp.model.testcase.TestCaseResult;
import org.edgegallery.atp.utils.TestCaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of validating .mf file content.
 */
public class MFContentTestCase extends TestCaseAbs {

    private static final Logger LOGGER = LoggerFactory.getLogger(MFContentTestCase.class);

    private TestCaseResult testCaseResult = new TestCaseResult();

    private static Set<String> field = new HashSet<String>() {
        {
            add("app_name");
            add("app_provider");
            add("app_archive_version");
            add("app_release_date_time");
            add("app_contact");
        }
    };

    @Override
    public TestCaseResult execute(String filePath) {
        try (ZipFile zipFile = new ZipFile(filePath)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.getName().split("/").length == 2 && TestCaseUtil.fileSuffixValidate("mf", entry.getName())) {
                    // some fields not exist in tosca.meta file
                    return TestCaseUtil.isExistAll(zipFile, entry, field)
                            ? setTestCaseResult(Constant.Status.SUCCESS, Constant.EMPTY, testCaseResult)
                            : setTestCaseResult(Constant.Status.FAILED, ExceptionConstant.MFContentTestCase.LOSS_FIELD,
                                    testCaseResult);
                }
            }
        } catch (IOException e) {
            LOGGER.error("TOSCAFileTestCase execute failed. {}", e.getMessage());
            return setTestCaseResult(Constant.Status.FAILED, ExceptionConstant.INNER_EXCEPTION, testCaseResult);
        }

        return setTestCaseResult(Constant.Status.FAILED, ExceptionConstant.MFContentTestCase.FILE_NOT_EXIST,
                testCaseResult);
    }

}
