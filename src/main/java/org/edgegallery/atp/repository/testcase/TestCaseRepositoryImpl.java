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

package org.edgegallery.atp.repository.testcase;

import java.util.List;
import org.edgegallery.atp.model.testcase.TestCase;
import org.edgegallery.atp.repository.mapper.TestCaseMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TestCaseRepositoryImpl implements TestCaseRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestCaseRepositoryImpl.class);

    @Autowired
    private TestCaseMapper testCaseMapper;

    @Override
    public List<TestCase> findAllTestCases(String type, String name, String verificationModel) {
        try {
            return testCaseMapper.findAllTestCases(type, name, verificationModel);
        } catch (Exception e) {
            LOGGER.error("findAllTestCases failed. {}", e);
            throw new IllegalArgumentException("findAllTestCases failed.");
        }
    }

    @Override
    public TestCase findByNameAndType(String name, String type) {
        try {
            return testCaseMapper.findByNameAndType(name, type);
        } catch (Exception e) {
            LOGGER.error("findByNameAndType failed. {}", e);
            throw new IllegalArgumentException("findByNameAndType failed.");
        }
    }

    @Override
    public void insert(TestCase testCase) {
        try {
            testCaseMapper.insert(testCase);
        } catch (Exception e) {
            LOGGER.error("insert test case failed. {}", e);
            throw new IllegalArgumentException("insert test case failed.");
        }
    }

    @Override
    public void update(TestCase testCase) {
        try {
            testCaseMapper.update(testCase);
        } catch (Exception e) {
            LOGGER.error("update test case failed. {}", e);
            throw new IllegalArgumentException("update test case failed.");
        }
    }

    @Override
    public int delete(String id) {
        try {
            return testCaseMapper.delete(id);
        } catch (Exception e) {
            LOGGER.error("delete test case failed. {}", e);
            throw new IllegalArgumentException("delete test case failed.");
        }
    }

    @Override
    public TestCase getTestCaseById(String id) {
        try {
            return testCaseMapper.getTestCaseById(id);
        } catch (Exception e) {
            LOGGER.error("getTestCaseById failed. {}", e);
            throw new IllegalArgumentException("getTestCaseById failed.");
        }
    }

    @Override
    public TestCase findByName(String name) {
        try {
            return testCaseMapper.findByName(name);
        } catch (Exception e) {
            LOGGER.error("findByName failed. {}", e);
            throw new IllegalArgumentException("findByName failed.");
        }
    }

    @Override
    public TestCase findByClassName(String className) {
        try {
            return testCaseMapper.findByClassName(className);
        } catch (Exception e) {
            LOGGER.error("findByClassName failed. {}", e);
            throw new IllegalArgumentException("findByClassName failed.");
        }
    }
}
