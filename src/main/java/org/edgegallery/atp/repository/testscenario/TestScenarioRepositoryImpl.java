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

package org.edgegallery.atp.repository.testscenario;

import java.util.List;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.model.testscenario.TestScenario;
import org.edgegallery.atp.repository.mapper.TestScenarioMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TestScenarioRepositoryImpl implements TestScenarioRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestScenarioRepositoryImpl.class);

    @Autowired
    TestScenarioMapper testScenarioMapper;

    @Override
    public void createTestScenario(TestScenario testScenario) {
        try {
            testScenarioMapper.createTestScenario(testScenario);
        } catch (Exception e) {
            LOGGER.error("insert test scenario failed. {}", e);
            throw new IllegalArgumentException("insert test scenario failed.");
        }
    }

    @Override
    public TestScenario getTestScenarioByName(String nameCh, String nameEn) {
        try {
            return testScenarioMapper.getTestScenarioByName(nameCh, nameEn);
        } catch (Exception e) {
            LOGGER.error("get test scenario by name {} failed. {}", nameEn, e);
            throw new IllegalArgumentException("get test scenario by name failed.");
        }
    }

    @Override
    public TestScenario getTestScenarioById(String id) {
        try {
            return testScenarioMapper.getTestScenarioById(id);
        } catch (Exception e) {
            LOGGER.error("get test scenario by id {} failed. {}", id, e);
            throw new IllegalArgumentException("get test scenario by id failed.");
        }
    }

    @Override
    public void updateTestScenario(TestScenario testScenario) {
        try {
            testScenarioMapper.updateTestScenario(testScenario);
        } catch (Exception e) {
            LOGGER.error("update test scenario failed. {}", e);
            throw new IllegalArgumentException("update test scenario failed.");
        }
    }

    @Override
    public void deleteTestScenario(String id) {
        try {
            testScenarioMapper.deleteTestScenario(id);
        } catch (Exception e) {
            LOGGER.error("delete test scenario {} failed. {}", id, e);
            throw new IllegalArgumentException("delete test scenario failed.");
        }
    }

    @Override
    public List<TestScenario> getAllTestScenarios(String locale, String name) {
        try {
            String nameCh = Constant.LOCALE_CH.equalsIgnoreCase(locale) ? name : null;
            String nameEn = Constant.LOCALE_EN.equalsIgnoreCase(locale) ? name : null;
            return testScenarioMapper.getAllTestScenario(nameCh, nameEn);
        } catch (Exception e) {
            LOGGER.error("get all test scenario failed. {}", e);
            throw new IllegalArgumentException("get all test scenario failed.");
        }
    }

    @Override
    public List<TestScenario> batchQueryTestScenario(List<String> ids) {
        try {
            return testScenarioMapper.batchQueryTestScenario(ids);
        } catch (Exception e) {
            LOGGER.error("batch query test scenario failed. {}", e);
            throw new IllegalArgumentException("batch query test scenario failed.");
        }
    }
}
