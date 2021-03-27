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

package org.edgegallery.atp.repository.testsuite;

import java.util.List;
import java.util.stream.Collectors;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.model.testsuite.TestSuite;
import org.edgegallery.atp.model.testsuite.TestSuitePo;
import org.edgegallery.atp.repository.mapper.TestSuiteMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TestSuiteRepositoryImpl implements TestSuiteRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestSuiteRepositoryImpl.class);

    @Autowired
    TestSuiteMapper testSuiteMapper;

    @Override
    public void createTestSuite(TestSuite testSuite) {
        try {
            testSuiteMapper.createTestSuite(new TestSuitePo(testSuite));
        } catch (Exception e) {
            LOGGER.error("insert test suite failed. {}", e);
            throw new IllegalArgumentException("insert test suite failed.");
        }
    }

    @Override
    public TestSuite getTestSuiteByName(String nameCh, String nameEn) {
        try {
            return null == testSuiteMapper.getTestSuiteByName(nameCh, nameEn) ? null
                    : testSuiteMapper.getTestSuiteByName(nameCh, nameEn).toDomain();
        } catch (Exception e) {
            LOGGER.error("get test suite by name {} failed. {}", nameEn, e);
            throw new IllegalArgumentException("get test suite by name failed.");
        }
    }

    @Override
    public TestSuite getTestSuiteById(String id) {
        try {
            return null == testSuiteMapper.getTestSuiteById(id) ? null
                    : testSuiteMapper.getTestSuiteById(id).toDomain();
        } catch (Exception e) {
            LOGGER.error("get test suite by id {} failed. {}", id, e);
            throw new IllegalArgumentException("get test suite by id failed.");
        }
    }

    @Override
    public void updateTestSuite(TestSuite testsuite) {
        try {
            testSuiteMapper.updateTestSuite(new TestSuitePo(testsuite));
        } catch (Exception e) {
            LOGGER.error("update test suite failed. {}", e);
            throw new IllegalArgumentException("update test suite failed.");
        }
    }

    @Override
    public void deleteTestSuite(String id) {
        try {
            testSuiteMapper.deleteTestSuite(id);
        } catch (Exception e) {
            LOGGER.error("delete test suite {} failed. {}", id, e);
            throw new IllegalArgumentException("delete test suite failed.");
        }
    }

    @Override
    public List<TestSuite> getAllTestSuites(String locale, String name, String id) {
        try {
            String nameCh = Constant.LOCALE_CH.equalsIgnoreCase(locale) ? name : null;
            String nameEn = Constant.LOCALE_EN.equalsIgnoreCase(locale) ? name : null;
            List<TestSuitePo> testSuitePoList = testSuiteMapper.getAllTestSuite(nameCh, nameEn, id);
            return null != testSuitePoList
                    ? testSuitePoList.stream().map(testSuitePo -> testSuitePo.toDomain()).collect(Collectors.toList())
                    : null;
        } catch (Exception e) {
            LOGGER.error("get all test suite failed. {}", e);
            throw new IllegalArgumentException("get all test suite failed.");
        }
    }

    @Override
    public List<TestSuite> batchQueryTestSuites(List<String> ids) {
        try {
            List<TestSuitePo> testSuitePoList = testSuiteMapper.batchQueryTestSuites(ids);
            return testSuitePoList.stream().map(testSuitePo -> testSuitePo.toDomain()).collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.error("batch query test suite failed. {}", e);
            throw new IllegalArgumentException("batch query test suite failed.");
        }
    }

}
