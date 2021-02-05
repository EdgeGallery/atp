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

import java.util.LinkedList;
import java.util.List;
import org.edgegallery.atp.model.testscenario.TestScenario;
import org.edgegallery.atp.model.testsuite.TestSuite;
import org.edgegallery.atp.repository.testscenario.TestScenarioRepository;
import org.edgegallery.atp.repository.testsuite.TestSuiteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("TestSuiteService")
public class TestSuiteServiceImpl implements TestSuiteService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestSuiteServiceImpl.class);

    @Autowired
    TestSuiteRepository testSuiteRepository;

    @Autowired
    TestScenarioRepository testScenarioRepository;

    @Override
    public TestSuite createTestSuite(TestSuite testSuite) {
        // nameCh or nameEn must exist one
        testSuite.setNameCh(null != testSuite.getNameCh() ? testSuite.getNameCh() : testSuite.getNameEn());
        testSuite.setNameEn(null != testSuite.getNameEn() ? testSuite.getNameEn() : testSuite.getNameCh());
        if (null == testSuite.getNameCh() && null == testSuite.getNameEn()) {
            throw new IllegalArgumentException("both nameCh and nameEn is null.");
        }
        if (null != testSuiteRepository.getTestSuiteByName(testSuite.getNameCh(), null)
                || null != testSuiteRepository.getTestSuiteByName(null, testSuite.getNameEn())) {
            throw new IllegalArgumentException("name of test suite already exist.");
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
            throw new IllegalArgumentException("chinese name of test suite already exist.");
        }
        if (!dbData.getNameEn().equalsIgnoreCase(testSuite.getNameEn())
                && null != testSuiteRepository.getTestSuiteByName(null, testSuite.getNameEn())) {
            throw new IllegalArgumentException("english name of test suite already exist.");
        }
        checkTestScenarioIdsExist(testSuite);
        testSuiteRepository.updateTestSuite(testSuite);
        LOGGER.info("update test suite successfully.");
        return testSuiteRepository.getTestSuiteById(testSuite.getId());
    }

    @Override
    public Boolean deleteTestSuite(String id) {
        testSuiteRepository.deleteTestSuite(id);
        LOGGER.info("delete test suite successfully.");
        return true;
    }

    @Override
    public TestSuite getTestSuite(String id) {
        TestSuite testSuite = testSuiteRepository.getTestSuiteById(id);
        LOGGER.info("get test suite successfully.");
        return testSuite;
    }

    @Override
    public List<TestSuite> queryAllTestSuite(String locale, String name, List<String> scenarioIdList) {
        List<TestSuite> result = new LinkedList<TestSuite>();
        if (null == scenarioIdList || 0 == scenarioIdList.size()) {
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
     * check test scenario ids is right
     * 
     * @param testSuite test suite info
     */
    private void checkTestScenarioIdsExist(TestSuite testSuite) {
        List<TestScenario> testScenarioList =
                testScenarioRepository.batchQueryTestScenario(testSuite.getScenarioIdList());
        if (testScenarioList.size() != testSuite.getScenarioIdList().size()) {
            throw new IllegalArgumentException("some test scenario ids do not exist.");
        }
    }
}
