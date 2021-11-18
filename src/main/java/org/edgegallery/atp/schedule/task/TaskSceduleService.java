/*
 * Copyright 2021 Huawei Technologies Co., Ltd.
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

package org.edgegallery.atp.schedule.task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.annotation.PostConstruct;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.model.file.AtpFile;
import org.edgegallery.atp.model.testcase.TestCase;
import org.edgegallery.atp.repository.file.FileRepository;
import org.edgegallery.atp.repository.task.TaskRepository;
import org.edgegallery.atp.repository.testcase.TestCaseRepository;
import org.edgegallery.atp.utils.FileChecker;
import org.edgegallery.atp.utils.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

@Service
public class TaskSceduleService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskSceduleService.class);

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    TestCaseRepository testCaseRepository;

    @Autowired
    FileRepository fileRepository;

    /**
     * put inner testCases and scenario icons in storage.
     */
    @PostConstruct
    public void handleData() {
        try {
            String basePath = System.getProperty("os.name").toLowerCase().contains("windows") ? PropertiesUtil
                .getProperties("workspace_base_dir_windows") : PropertiesUtil.getProperties("workspace_base_dir_linux");

            String baseTestCasePath = basePath.concat(Constant.SLASH).concat(Constant.TEST_CASE_DIR);
            handleTestCase(new File(baseTestCasePath.concat(File.separator).concat("CommunitySecurity")));
            handleTestCase(new File(baseTestCasePath.concat(File.separator).concat("CommunitySandbox")));
            handleTestCase(new File(baseTestCasePath.concat(File.separator).concat("CommunityCompliance")));
            handleTestCase(new File(baseTestCasePath.concat(File.separator).concat("CommunityPerformance")));
            handleTestCase(new File(baseTestCasePath.concat(File.separator).concat("AOperatorSecurity")));
            handleTestCase(new File(baseTestCasePath.concat(File.separator).concat("AOperatorCompliance")));

            File iconDir = new File(basePath.concat(Constant.SLASH).concat(Constant.ICON));
            handleIconFile(iconDir);
        } catch (FileNotFoundException e) {
            LOGGER.error("resource testCase file can not be found");
        } catch (IOException e) {
            LOGGER.error("copy test case or icon to path failed.");
        }
    }

    /**
     * save icon file.
     *
     * @param iconDir iconDir
     * @throws IOException IOException
     */
    private void handleIconFile(File iconDir) throws IOException {
        if (!iconDir.exists() || null == iconDir.listFiles()) {
            return;
        }
        for (File icon : iconDir.listFiles()) {
            String iconPath = Constant.BASIC_ICON_PATH + icon.getName();
            FileChecker.createFile(iconPath);
            File result = new File(iconPath);
            FileCopyUtils.copy(icon, result);

            String name = icon.getName();
            String scenarioId = name.substring(name.indexOf(Constant.UNDER_LINE) + 1, name.indexOf(Constant.DOT));
            AtpFile fileFromDb = fileRepository.getFileContent(scenarioId, Constant.FILE_TYPE_SCENARIO);
            if (null == fileFromDb) {
                AtpFile atpFile = new AtpFile(scenarioId, Constant.FILE_TYPE_SCENARIO, taskRepository.getCurrentDate(),
                    iconPath);
                fileRepository.insertFile(atpFile);
            }
        }
    }

    /**
     * save test case to env path.
     *
     * @param fileDir src dir
     * @throws IOException IOException
     */
    private void handleTestCase(File fileDir) throws IOException {
        if (!fileDir.exists() || null == fileDir.listFiles()) {
            return;
        }
        for (File file : fileDir.listFiles()) {
            TestCase testCase = testCaseRepository
                .findByName(null, file.getName().substring(0, file.getName().indexOf(Constant.DOT)));
            saveTestCaseFile(testCase, file);
        }
    }

    /**
     * save test case to path.
     *
     * @param testCase testCase
     * @param file file
     * @throws IOException IOException
     */
    private void saveTestCaseFile(TestCase testCase, File file) throws IOException {
        if (null != testCase) {
            String filePath = Constant.BASIC_TEST_CASE_PATH + testCase.getNameEn() + Constant.UNDER_LINE + testCase
                .getId();
            FileChecker.createFile(filePath);
            File result = new File(filePath);
            FileCopyUtils.copy(file, result);

            testCase.setFilePath(filePath);
            testCaseRepository.update(testCase);
        } else {
            LOGGER.error("init test case failed, find by name from db is null.");
        }
    }
}
