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

package org.edgegallery.atp.repository.testscenario;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.constant.ErrorCode;
import org.edgegallery.atp.model.testscenario.TestScenario;
import org.edgegallery.atp.repository.mapper.TestScenarioMapper;
import org.edgegallery.atp.utils.exception.IllegalRequestException;
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
            throw new IllegalRequestException(String.format(ErrorCode.DB_ERROR_MSG, "insert test scenario failed"),
                    ErrorCode.DB_ERROR, new ArrayList<String>(Arrays.asList("insert test scenario failed")));
        }
    }

    @Override
    public TestScenario getTestScenarioByName(String nameCh, String nameEn) {
        try {
            return testScenarioMapper.getTestScenarioByName(nameCh, nameEn);
        } catch (Exception e) {
            LOGGER.error("get test scenario by name {} failed. {}", nameEn, e);
            throw new IllegalRequestException(String.format(ErrorCode.DB_ERROR_MSG, "get test scenario by name failed"),
                    ErrorCode.DB_ERROR, new ArrayList<String>(Arrays.asList("get test scenario by name failed")));
        }
    }

    @Override
    public TestScenario getTestScenarioById(String id) {
        try {
            return testScenarioMapper.getTestScenarioById(id);
        } catch (Exception e) {
            LOGGER.error("get test scenario by id {} failed. {}", id, e);
            throw new IllegalRequestException(String.format(ErrorCode.DB_ERROR_MSG, "get test scenario by id failed"),
                    ErrorCode.DB_ERROR, new ArrayList<String>(Arrays.asList("get test scenario by id failed")));
        }
    }

    @Override
    public void updateTestScenario(TestScenario testScenario) {
        try {
            testScenarioMapper.updateTestScenario(testScenario);
        } catch (Exception e) {
            LOGGER.error("update test scenario failed. {}", e);
            throw new IllegalRequestException(String.format(ErrorCode.DB_ERROR_MSG, "update test scenario failed"),
                    ErrorCode.DB_ERROR, new ArrayList<String>(Arrays.asList("update test scenario failed")));
        }
    }

    @Override
    public void deleteTestScenario(String id) {
        try {
            testScenarioMapper.deleteTestScenario(id);
        } catch (Exception e) {
            LOGGER.error("delete test scenario {} failed. {}", id, e);
            throw new IllegalRequestException(String.format(ErrorCode.DB_ERROR_MSG, "delete test scenario failed"),
                    ErrorCode.DB_ERROR, new ArrayList<String>(Arrays.asList("delete test scenario failed")));
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
            throw new IllegalRequestException(String.format(ErrorCode.DB_ERROR_MSG, "get all test scenarios failed"),
                ErrorCode.DB_ERROR, new ArrayList<String>(Arrays.asList("get all test scenarios failed")));
        }
    }

    @Override
    public int countTotal(String locale, String name) {
        try {
            String nameCh = Constant.LOCALE_CH.equalsIgnoreCase(locale) ? name : null;
            String nameEn = Constant.LOCALE_EN.equalsIgnoreCase(locale) ? name : null;
            return testScenarioMapper.countTotal(nameCh, nameEn);
        } catch (Exception e) {
            LOGGER.error("get test scenario total count failed. {}", e);
            throw new IllegalRequestException(
                String.format(ErrorCode.DB_ERROR_MSG, "get test scenario total count failed"), ErrorCode.DB_ERROR,
                new ArrayList<String>(Arrays.asList("get test scenario total count failed")));
        }
    }

    @Override
    public List<TestScenario> getAllWithPagination(int limit, int offset, String locale, String name) {
        try {
            String nameCh = Constant.LOCALE_CH.equalsIgnoreCase(locale) ? name : null;
            String nameEn = Constant.LOCALE_EN.equalsIgnoreCase(locale) ? name : null;
            return testScenarioMapper.getAllWithPagination(limit, offset, nameCh, nameEn);
        } catch (Exception e) {
            LOGGER.error("get all test scenarios by pagination failed. {}", e);
            throw new IllegalRequestException(
                String.format(ErrorCode.DB_ERROR_MSG, "get all test scenarios by pagination failed"),
                ErrorCode.DB_ERROR,
                new ArrayList<String>(Arrays.asList("get all test scenarios by pagination failed")));
        }
    }

    @Override
    public List<TestScenario> batchQueryTestScenario(List<String> ids) {
        try {
            return testScenarioMapper.batchQueryTestScenario(ids);
        } catch (Exception e) {
            LOGGER.error("batch query test scenario failed. {}", e);
            throw new IllegalRequestException(
                String.format(ErrorCode.DB_ERROR_MSG, "batch query test scenarios failed"), ErrorCode.DB_ERROR,
                new ArrayList<String>(Arrays.asList("batch query test scenarios failed")));
        }
    }
}
