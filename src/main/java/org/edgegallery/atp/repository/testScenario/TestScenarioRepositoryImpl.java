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
package org.edgegallery.atp.repository.testScenario;

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
    public TestScenario getTestScenarioByName(String nameZh, String nameEn) {
        try {
            return testScenarioMapper.getTestScenarioByName(nameZh, nameEn);
        } catch (Exception e) {
            LOGGER.error("get test scenario by name {} failed. {}", nameEn, e);
            throw new IllegalArgumentException("get test scenario by name failed.");
        }
    }

}
