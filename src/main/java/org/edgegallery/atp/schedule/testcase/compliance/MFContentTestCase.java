package org.edgegallery.atp.schedule.testcase.compliance;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
 * Implementation of validating .mf file content.
 */
public class MFContentTestCase extends TestCaseAbs {

    private static final Logger LOGGER = LoggerFactory.getLogger(MFContentTestCase.class);

    private TestCaseResult testCaseResult = new TestCaseResult();

    private static Set<String> field = new HashSet<String>() {
        {
            add("app_product_name");
            add("app_provider_id");
            add("app_package_version");
            add("app_release_data_time");
            add("app_package_description");
        }
    };

    @Override
    public TestCaseResult execute(String filePath, Map<String, String> context) {
        try (ZipFile zipFile = new ZipFile(filePath)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.getName().split(Constant.SLASH).length == 2
                        && TestCaseUtil.fileSuffixValidate("mf", entry.getName())) {
                    // some fields not exist in tosca.meta file
                    return TestCaseUtil.isExistAll(zipFile, entry, field)
                            ? setTestCaseResult(Constant.SUCCESS, Constant.EMPTY, testCaseResult)
                            : setTestCaseResult(Constant.FAILED, ExceptionConstant.MF_LOSS_FIELD, testCaseResult);
                }
            }
        } catch (IOException e) {
            LOGGER.error("TOSCAFileTestCase execute failed. {}", e.getMessage());
            return setTestCaseResult(Constant.FAILED, ExceptionConstant.INNER_EXCEPTION, testCaseResult);
        }

        return setTestCaseResult(Constant.FAILED, ExceptionConstant.FILE_NOT_EXIST, testCaseResult);
    }

}
