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
package org.edgegallery.atp.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.model.file.ATPFile;
import org.edgegallery.atp.model.testcase.TestCase;
import org.edgegallery.atp.model.testscenario.TestScenario;
import org.edgegallery.atp.model.testscenario.testcase.AllTestScenarios;
import org.edgegallery.atp.model.testscenario.testcase.AllTestSuites;
import org.edgegallery.atp.model.testsuite.TestSuite;
import org.edgegallery.atp.repository.file.FileRepository;
import org.edgegallery.atp.repository.task.TaskRepository;
import org.edgegallery.atp.repository.testcase.TestCaseRepository;
import org.edgegallery.atp.repository.testscenario.TestScenarioRepository;
import org.edgegallery.atp.repository.testsuite.TestSuiteRepository;
import org.edgegallery.atp.utils.FileChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service("TestScenarioService")
public class TestScenarioServiceImpl implements TestScenarioService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestScenarioServiceImpl.class);

    @Autowired
    TestScenarioRepository testScenarioRepository;

    @Autowired
    FileRepository fileRepository;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    TestCaseRepository testCaseRepository;

    @Autowired
    TestSuiteRepository testSuiteRepository;

    @Override
    public TestScenario createTestScenario(TestScenario testScenario, MultipartFile icon) {
        // nameCh or nameEn must exist one
        testScenario.setNameCh(StringUtils.isNoneBlank(testScenario.getNameCh()) ? testScenario.getNameCh()
                : testScenario.getNameEn());
        testScenario.setNameEn(StringUtils.isNoneBlank(testScenario.getNameEn()) ? testScenario.getNameEn()
                : testScenario.getNameCh());
        if (StringUtils.isEmpty(testScenario.getNameCh()) && StringUtils.isEmpty(testScenario.getNameEn())) {
            String msg = "both nameCh and nameEn is empty.";
            LOGGER.error(msg);
            throw new IllegalArgumentException(msg);
        }
        checkNameExists(testScenario);
        String iconName = icon.getOriginalFilename();
        String suffix = iconName.substring(icon.getOriginalFilename().indexOf(Constant.DOT) + 1);
        String filePath = Constant.BASIC_ICON_PATH.concat(Constant.FILE_TYPE_SCENARIO).concat(Constant.UNDER_LINE)
                .concat(testScenario.getId()).concat(Constant.DOT).concat(suffix);
        FileChecker.copyFileToDir(icon, filePath);
        ATPFile atpFile = new ATPFile(testScenario.getId(), Constant.FILE_TYPE_SCENARIO,
                taskRepository.getCurrentDate(), filePath);
        fileRepository.insertFile(atpFile);
        testScenarioRepository.createTestScenario(testScenario);
        LOGGER.info("create test scenario successfully.");
        return testScenario;
    }

    @Override
    public TestScenario updateTestScenario(TestScenario testScenario, MultipartFile icon) {
        TestScenario dbData = testScenarioRepository.getTestScenarioById(testScenario.getId());
        if (!dbData.getNameCh().equalsIgnoreCase(testScenario.getNameCh())
                && null != testScenarioRepository.getTestScenarioByName(testScenario.getNameCh(), null)) {
            String msg = "chinese name of test scenario already exist.";
            LOGGER.error(msg);
            throw new IllegalArgumentException(msg);
        }
        if (!dbData.getNameEn().equalsIgnoreCase(testScenario.getNameEn())
                && null != testScenarioRepository.getTestScenarioByName(null, testScenario.getNameEn())) {
            String msg = "english name of test suite already exist.";
            LOGGER.error(msg);
            throw new IllegalArgumentException("english name of test suite already exist.");
        }
        testScenarioRepository.updateTestScenario(testScenario);

        if (null != icon && StringUtils.isNotBlank(icon.getOriginalFilename()) && StringUtils.isNotBlank(icon.getName())
                && 0 != (int) icon.getSize()) {
            try {
                ATPFile file = fileRepository.getFileContent(testScenario.getId(), Constant.FILE_TYPE_SCENARIO);
                String filePath = file.getFilePath();
                new File(filePath).delete();
                File result = new File(filePath);
                icon.transferTo(result);
            } catch (IOException e) {
                LOGGER.error("file store failed, {}", e);
            }
        }
        LOGGER.info("update test scenario successfully.");
        return testScenarioRepository.getTestScenarioById(testScenario.getId());
    }

    @Override
    public Boolean deleteTestScenario(String id) {
        List<TestSuite> testSuiteList = testSuiteRepository.getAllTestSuites(null, null, id);
        if (CollectionUtils.isNotEmpty(testSuiteList)) {
            LOGGER.error("scenario id {} is used by some test suites, so can not be delete.", id);
            throw new IllegalArgumentException("this scenario is used by some test suites, so can not be delete.");
        }
        ATPFile file = fileRepository.getFileContent(id, Constant.FILE_TYPE_SCENARIO);
        new File(file.getFilePath()).delete();
        testScenarioRepository.deleteTestScenario(id);
        LOGGER.info("delete test scenario successfully.");
        return true;
    }

    @Override
    public TestScenario getTestScenario(String id) {
        TestScenario result = testScenarioRepository.getTestScenarioById(id);
        LOGGER.info("get test scenario by id successfully.");
        return result;
    }

    @Override
    public List<TestScenario> queryAllTestScenario(String locale, String name) {
        List<TestScenario> testScenarioList = testScenarioRepository.getAllTestScenarios(locale, name);
        LOGGER.info("get all test scenarios successfully.");
        return testScenarioList;
    }

    @Override
    public List<AllTestScenarios> getTestCasesByScenarioIds(List<String> ids) {
        List<AllTestScenarios> result = new ArrayList<AllTestScenarios>();
        ids.forEach(scenarioId -> {
            TestScenario testScenario = testScenarioRepository.getTestScenarioById(scenarioId);
            if (null == testScenario) {
                LOGGER.error("scenarioId {} not exists", scenarioId);
                throw new IllegalArgumentException("scenarioId not exists.");
            }

            AllTestScenarios allTestScenarios = new AllTestScenarios(testScenario);
            List<AllTestSuites> testSuites = new ArrayList<AllTestSuites>();

            List<TestSuite> testSuiteList = testSuiteRepository.getAllTestSuites(null, null, scenarioId);
            testSuiteList.forEach(testSuite -> {
                AllTestSuites allTestSuite = new AllTestSuites(testSuite);
                List<TestCase> testCaseList = testCaseRepository.findAllTestCases(null, null, null, testSuite.getId());
                allTestSuite.setTestCases(testCaseList);
                testSuites.add(allTestSuite);
            });
            allTestScenarios.setTestSuites(testSuites);
            result.add(allTestScenarios);
        });
        return result;
    }

    private void checkNameExists(TestScenario testScenario) {
        if (null != testScenarioRepository.getTestScenarioByName(testScenario.getNameCh(), null)
                || null != testScenarioRepository.getTestScenarioByName(null, testScenario.getNameEn())) {
            String msg = "name of test scenario already exist.";
            LOGGER.error(msg);
            throw new IllegalArgumentException(msg);
        }
    }

}
