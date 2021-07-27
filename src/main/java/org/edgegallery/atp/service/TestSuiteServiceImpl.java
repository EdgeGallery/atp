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

package org.edgegallery.atp.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.edgegallery.atp.constant.ErrorCode;
import org.edgegallery.atp.model.testcase.TestCase;
import org.edgegallery.atp.model.testscenario.TestScenario;
import org.edgegallery.atp.model.testsuite.TestSuite;
import org.edgegallery.atp.repository.task.TaskRepository;
import org.edgegallery.atp.repository.testcase.TestCaseRepository;
import org.edgegallery.atp.repository.testscenario.TestScenarioRepository;
import org.edgegallery.atp.repository.testsuite.TestSuiteRepository;
import org.edgegallery.atp.utils.CommonUtil;
import org.edgegallery.atp.utils.exception.FileNotExistsException;
import org.edgegallery.atp.utils.exception.IllegalRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service("TestSuiteService")
public class TestSuiteServiceImpl implements TestSuiteService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestSuiteServiceImpl.class);

    @Autowired
    TestSuiteRepository testSuiteRepository;

    @Autowired
    TestScenarioRepository testScenarioRepository;

    @Autowired
    TestCaseRepository testCaseRepository;

    @Autowired
    TaskRepository taskRepository;

    @Override
    public TestSuite createTestSuite(TestSuite testSuite) {
        // nameCh or nameEn must exist one
        CommonUtil.nameExistenceValidation(testSuite.getNameCh(), testSuite.getNameEn());
        testSuite
            .setNameCh(StringUtils.isNotBlank(testSuite.getNameCh()) ? testSuite.getNameCh() : testSuite.getNameEn());
        testSuite.setNameEn(
                StringUtils.isNotBlank(testSuite.getNameEn()) ? testSuite.getNameEn() : testSuite.getNameCh());

        testSuite.setDescriptionCh(StringUtils.isNotBlank(testSuite.getDescriptionCh()) ? testSuite.getDescriptionCh()
                : testSuite.getDescriptionEn());
        testSuite.setDescriptionEn(StringUtils.isNotBlank(testSuite.getDescriptionEn()) ? testSuite.getDescriptionEn()
                : testSuite.getDescriptionCh());
        testSuite.setCreateTime(taskRepository.getCurrentDate());

        if (null != testSuiteRepository.getTestSuiteByName(testSuite.getNameCh(), null)
                || null != testSuiteRepository.getTestSuiteByName(null, testSuite.getNameEn())) {
            LOGGER.error("name of test suite already exist.");
            String param = testSuite.getNameCh() + " or " + testSuite.getNameEn();
            throw new IllegalRequestException(String.format(ErrorCode.NAME_EXISTS_MSG, param), ErrorCode.NAME_EXISTS,
                new ArrayList<String>(Arrays.asList(param)));
        }
        checkTestScenarioIdsExist(testSuite);
        testSuiteRepository.createTestSuite(testSuite);
        LOGGER.info("create test suite successfully.");
        return testSuite;
    }

    @Override
    public TestSuite updateTestSuite(TestSuite testSuite) {
        TestSuite dbData = testSuiteRepository.getTestSuiteById(testSuite.getId());
        if (!dbData.getNameCh().equalsIgnoreCase(testSuite.getNameCh())
                && null != testSuiteRepository.getTestSuiteByName(testSuite.getNameCh(), null)) {
            LOGGER.error("chinese name of test suite already exist.");
            throw new IllegalRequestException(String.format(ErrorCode.NAME_EXISTS_MSG, testSuite.getNameCh()),
                ErrorCode.NAME_EXISTS, new ArrayList<String>(Arrays.asList(testSuite.getNameCh())));
        }
        if (!dbData.getNameEn().equalsIgnoreCase(testSuite.getNameEn())
                && null != testSuiteRepository.getTestSuiteByName(null, testSuite.getNameEn())) {
            LOGGER.error("english name of test suite already exist.");
            throw new IllegalRequestException(String.format(ErrorCode.NAME_EXISTS_MSG, testSuite.getNameEn()),
                ErrorCode.NAME_EXISTS, new ArrayList<String>(Arrays.asList(testSuite.getNameEn())));
        }
        checkTestScenarioIdsExist(testSuite);
        testSuiteRepository.updateTestSuite(testSuite);
        LOGGER.info("update test suite successfully.");
        return testSuiteRepository.getTestSuiteById(testSuite.getId());
    }

    @Override
    public Boolean deleteTestSuite(String id) {
        List<TestCase> testCaseList = testCaseRepository.findAllTestCases(null, null, null, id);
        if (!CollectionUtils.isEmpty(testCaseList)) {
            LOGGER.error("test suite {} is used by some test cases, can not be deleted.", id);
            throw new IllegalRequestException(ErrorCode.TEST_SUITE_IS_CITED_MSG, ErrorCode.TEST_SUITE_IS_CITED, null);
        }
        testSuiteRepository.deleteTestSuite(id);
        LOGGER.info("delete test suite successfully.");
        return true;
    }

    @Override
    public TestSuite getTestSuite(String id) throws FileNotExistsException {
        TestSuite testSuite = testSuiteRepository.getTestSuiteById(id);
        if (null == testSuite) {
            LOGGER.error("test suite id does not exists: {}", id);
            throw new FileNotExistsException(String.format(ErrorCode.NOT_FOUND_EXCEPTION_MSG, "test suite id"),
                ErrorCode.NOT_FOUND_EXCEPTION, new ArrayList<String>(Arrays.asList("test suite id")));
        }
        LOGGER.info("get test suite successfully.");
        return testSuite;
    }

    @Override
    public List<TestSuite> queryAllTestSuite(String locale, String name, List<String> scenarioIdList) {
        List<TestSuite> result = new LinkedList<TestSuite>();
        if (null == scenarioIdList || CollectionUtils.isEmpty(scenarioIdList)) {
            result = testSuiteRepository.getAllTestSuites(locale, name, null);
        } else {
            List<TestSuite> testSuiteList = testSuiteRepository.getAllTestSuites(locale, name, scenarioIdList.get(0));
            for (TestSuite testSuite : testSuiteList) {
                boolean isSatisfy = true;
                for (String id : scenarioIdList) {
                    if (!testSuite.getScenarioIdList().contains(id)) {
                        isSatisfy = false;
                        break;
                    }
                }
                if (isSatisfy) {
                    result.add(testSuite);
                }
            }
        }
        LOGGER.info("query all test suites successfully.");
        return result;
    }

    /**
     * check test scenario ids is right.
     * 
     * @param testSuite test suite info
     */
    private void checkTestScenarioIdsExist(TestSuite testSuite) {
        List<TestScenario> testScenarioList =
                testScenarioRepository.batchQueryTestScenario(testSuite.getScenarioIdList());
        if (testScenarioList.size() != testSuite.getScenarioIdList().size()) {
            LOGGER.error("some test scenario ids do not exist.");
            throw new IllegalRequestException(
                String.format(ErrorCode.NOT_FOUND_EXCEPTION_MSG, "some test scenario ids."),
                ErrorCode.NOT_FOUND_EXCEPTION, new ArrayList<String>(Arrays.asList("some test scenario ids.")));
        }
    }
}
