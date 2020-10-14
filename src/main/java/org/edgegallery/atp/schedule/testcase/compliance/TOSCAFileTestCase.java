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
 * Implementation of validating TOSCA.meta file.
 */
public class TOSCAFileTestCase extends TestCaseAbs {

    private static final Logger LOGGER = LoggerFactory.getLogger(TOSCAFileTestCase.class);
    private static final String TOSCA_META = "TOSCA.meta";

    private TestCaseResult testCaseResult = new TestCaseResult();
    private static Set<String> pathSet = new HashSet<String>();
    private static Set<String> field = new HashSet<String>() {
        {
            add("Entry-Definitions");
            add("ETSI-Entry-Manifest");
            add("Entry-Tests");
            add("ETSI-Entry-Change-Log");
            add("Entry-Helm-Package");
        }
    };

    @Override
    public TestCaseResult execute(String filePath, Map<String, String> context) {
        Set<String> sourcePathSet = new HashSet<String>();
        boolean isExistTosca = false;
        try (ZipFile zipFile = new ZipFile(filePath)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String entryName = entry.getName();
                String path = entryName.substring(entryName.indexOf("/") + 1).trim();

                // suit for pattern of Artifacts/test,not Artifacts/test/
                pathSet.add(TestCaseUtil.removeLastSlash(path));

                if (TOSCA_META.equals(entryName.substring(entryName.lastIndexOf("/") + 1).trim())) {
                    isExistTosca = true;
                    // some fields not exist in tosca.meta file
                    if (!TestCaseUtil.isExistAll(zipFile, entry, field)) {
                        return setTestCaseResult(Constant.Status.FAILED, ExceptionConstant.TOSCAFileTestCase.LOSS_FIELD,
                                testCaseResult);
                    }
                    sourcePathSet = TestCaseUtil.getPathSet(zipFile, entry, field);
                }
            }
        } catch (IOException e) {
            LOGGER.error("TOSCAFileTestCase execute failed. {}", e.getMessage());
            return setTestCaseResult(Constant.Status.FAILED, ExceptionConstant.INNER_EXCEPTION, testCaseResult);
        }

        return isExistTosca == false
                ? setTestCaseResult(Constant.Status.FAILED, ExceptionConstant.TOSCAFileTestCase.TOSCA_FILE_NOT_EXISTS,
                        testCaseResult)
                : pathSet.containsAll(sourcePathSet)
                        ? setTestCaseResult(Constant.Status.SUCCESS, Constant.EMPTY, testCaseResult)
                        : setTestCaseResult(Constant.Status.FAILED, ExceptionConstant.TOSCAFileTestCase.FILE_NOT_EXIT,
                                testCaseResult);
    }

}
