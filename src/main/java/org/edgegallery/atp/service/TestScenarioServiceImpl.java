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

import java.util.List;
import org.edgegallery.atp.model.testscenario.TestScenario;
import org.edgegallery.atp.repository.testScenario.TestScenarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("TestScenarioService")
public class TestScenarioServiceImpl implements TestScenarioService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestScenarioServiceImpl.class);
    @Autowired
    TestScenarioRepository testScenarioRepository;

    @Override
    public TestScenario creatTestScenario(TestScenario testScenario) {
        checkNameExists(testScenario);
        testScenarioRepository.createTestScenario(testScenario);
        LOGGER.info("create test scenario successfully.");
        return testScenario;
    }

    @Override
    public TestScenario updateTestScenario(TestScenario testScenario) {
        checkNameExists(testScenario);
        testScenarioRepository.updateTestScenario(testScenario);
        LOGGER.info("update test scenario successfully.");
        return testScenarioRepository.getTestScenarioById(testScenario.getId());
    }

    @Override
    public Boolean deleteTestScenario(String id) {
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

    private void checkNameExists(TestScenario testScenario) {
        if (null != testScenarioRepository.getTestScenarioByName(testScenario.getNameCh(), null)
                || null != testScenarioRepository.getTestScenarioByName(null, testScenario.getNameEn())) {
            throw new IllegalArgumentException("name of test scenario already exist.");
        }
    }

}
