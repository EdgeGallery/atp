/*
 * Copyright 2020 Huawei Technologies Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

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
                        return setTestCaseResult(Constant.FAILED, ExceptionConstant.TOSCA_LOSS_FIELD, testCaseResult);
                    }
                    sourcePathSet = TestCaseUtil.getPathSet(zipFile, entry, field);
                }
            }
        } catch (IOException e) {
            LOGGER.error("TOSCAFileTestCase execute failed. {}", e.getMessage());
            return setTestCaseResult(Constant.FAILED, ExceptionConstant.INNER_EXCEPTION, testCaseResult);
        }

        return isExistTosca == false
                ? setTestCaseResult(Constant.FAILED, ExceptionConstant.TOSCA_FILE_NOT_EXISTS, testCaseResult)
                : pathSet.containsAll(sourcePathSet)
                        ? setTestCaseResult(Constant.SUCCESS, Constant.EMPTY, testCaseResult)
                        : setTestCaseResult(Constant.FAILED, ExceptionConstant.FILE_NOT_EXIT, testCaseResult);
    }

}
