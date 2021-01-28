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
 * Implementation of validating sourcePath availability.
 */
public class SourcePathTestCase extends TestCaseAbs {

    private static final Logger LOGGER = LoggerFactory.getLogger(SourcePathTestCase.class);

    private static Set<String> pathSet = new HashSet<String>();

    private TestCaseResult testCaseResult = new TestCaseResult();

    @Override
    public TestCaseResult execute(String filePath, Map<String, String> context) {
        Set<String> sourcePathSet = new HashSet<String>();
        try (ZipFile zipFile = new ZipFile(filePath)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                pathSet.add(TestCaseUtil.removeLastSlash(entry.getName()));
                // root directory and file is end of mf
                if (entry.getName().split(Constant.SLASH).length == 1
                        && TestCaseUtil.fileSuffixValidate("mf", entry.getName())) {
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
            return setTestCaseResult(Constant.FAILED, ExceptionConstant.INNER_EXCEPTION, testCaseResult);
        }
        return pathSet.containsAll(sourcePathSet) ? setTestCaseResult(Constant.SUCCESS, Constant.EMPTY, testCaseResult)
                : setTestCaseResult(Constant.FAILED, ExceptionConstant.SOURCE_PATH_FILE_NOT_EXISTS, testCaseResult);
    }

}
