/*
 * Copyright 2020-2021 Huawei Technologies Co., Ltd.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.constant.ErrorCode;
import org.edgegallery.atp.model.testcase.TestCase;
import org.edgegallery.atp.model.testcase.TestCasePo;
import org.edgegallery.atp.repository.mapper.TestCaseMapper;
import org.edgegallery.atp.utils.exception.IllegalRequestException;
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
    public List<TestCase> findAllTestCases(String type, String locale, String name, String testSuiteId) {
        try {
            String nameCh = Constant.LOCALE_CH.equalsIgnoreCase(locale) ? name : null;
            String nameEn = Constant.LOCALE_EN.equalsIgnoreCase(locale) ? name : null;
            List<TestCasePo> testCasePoList = testCaseMapper.findAllTestCases(type, nameCh, nameEn, testSuiteId);
            return null == testCasePoList
                ? null
                : testCasePoList.stream().map(testCasePo -> testCasePo.toDomain()).collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.error("findAllTestCases failed. {}", e);
            throw new IllegalRequestException(String.format(ErrorCode.DB_ERROR_MSG, "findAllTestCases failed"),
                ErrorCode.DB_ERROR, new ArrayList<String>(Arrays.asList("findAllTestCases failed")));
        }
    }

    @Override
    public List<TestCase> findAllTestCasesByPaginition(String type, String locale, String name, String testSuiteId,
        int limit, int offset) {
        try {
            String nameCh = Constant.LOCALE_CH.equalsIgnoreCase(locale) ? name : null;
            String nameEn = Constant.LOCALE_EN.equalsIgnoreCase(locale) ? name : null;
            List<TestCasePo> testCasePoList = testCaseMapper
                .findAllTestCasesByPaginition(type, nameCh, nameEn, testSuiteId, limit, offset);
            return null == testCasePoList
                ? null
                : testCasePoList.stream().map(testCasePo -> testCasePo.toDomain()).collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.error("find all test cases by pagination failed. {}", e);
            throw new IllegalRequestException(
                String.format(ErrorCode.DB_ERROR_MSG, "find all test cases by pagination failed"), ErrorCode.DB_ERROR,
                new ArrayList<String>(Arrays.asList("find all test cases by pagination failed")));
        }
    }

    @Override
    public int countTotal(String type, String locale, String name, String testSuiteId) {
        try {
            String nameCh = Constant.LOCALE_CH.equalsIgnoreCase(locale) ? name : null;
            String nameEn = Constant.LOCALE_EN.equalsIgnoreCase(locale) ? name : null;
            return testCaseMapper.countTotal(type, nameCh, nameEn, testSuiteId);
        } catch (Exception e) {
            LOGGER.error("get test case total count failed. {}", e);
            throw new IllegalRequestException(String.format(ErrorCode.DB_ERROR_MSG, "get test case total count failed"),
                ErrorCode.DB_ERROR, new ArrayList<String>(Arrays.asList("get test case total count failed")));
        }
    }

    @Override
    public TestCase findByNameAndType(String name, String type) {
        try {
            return null == testCaseMapper.findByNameAndType(name, type)
                ? null
                : testCaseMapper.findByNameAndType(name, type).toDomain();
        } catch (Exception e) {
            LOGGER.error("findByNameAndType failed. {}", e);
            throw new IllegalRequestException(String.format(ErrorCode.DB_ERROR_MSG, "testCaseFindByNameAndType failed"),
                ErrorCode.DB_ERROR, new ArrayList<String>(Arrays.asList("testCaseFindByNameAndType failed")));
        }
    }

    @Override
    public void insert(TestCase testCase) {
        try {
            testCaseMapper.insert(testCase.of());
        } catch (Exception e) {
            LOGGER.error("insert test case failed. {}", e);
            throw new IllegalRequestException(String.format(ErrorCode.DB_ERROR_MSG, "insert test case failed"),
                ErrorCode.DB_ERROR, new ArrayList<String>(Arrays.asList("insert test case failed")));
        }
    }

    @Override
    public void update(TestCase testCase) {
        try {
            testCaseMapper.update(testCase.of());
        } catch (Exception e) {
            LOGGER.error("update test case failed. {}", e);
            throw new IllegalRequestException(String.format(ErrorCode.DB_ERROR_MSG, "update test case failed"),
                ErrorCode.DB_ERROR, new ArrayList<String>(Arrays.asList("update test case failed")));
        }
    }

    @Override
    public int delete(String id) {
        try {
            return testCaseMapper.delete(id);
        } catch (Exception e) {
            LOGGER.error("delete test case failed. {}", e);
            throw new IllegalRequestException(String.format(ErrorCode.DB_ERROR_MSG, "delete test case failed"),
                ErrorCode.DB_ERROR, new ArrayList<String>(Arrays.asList("delete test case failed")));
        }
    }

    @Override
    public TestCase getTestCaseById(String id) {
        try {
            return null == testCaseMapper.getTestCaseById(id) ? null : testCaseMapper.getTestCaseById(id).toDomain();
        } catch (Exception e) {
            LOGGER.error("getTestCaseById failed. {}", e);
            throw new IllegalRequestException(String.format(ErrorCode.DB_ERROR_MSG, "getTestCaseById failed"),
                ErrorCode.DB_ERROR, new ArrayList<String>(Arrays.asList("getTestCaseById failed")));
        }
    }

    @Override
    public TestCase findByName(String nameCh, String nameEn) {
        try {
            return null == testCaseMapper.findByName(nameCh, nameEn)
                ? null
                : testCaseMapper.findByName(nameCh, nameEn).toDomain();
        } catch (Exception e) {
            LOGGER.error("findByName failed. {}", e);
            throw new IllegalRequestException(String.format(ErrorCode.DB_ERROR_MSG, "test case findByName failed"),
                ErrorCode.DB_ERROR, new ArrayList<String>(Arrays.asList("test case findByName failed")));
        }
    }

    @Override
    public TestCase findByClassName(String className) {
        try {
            return null == testCaseMapper.findByClassName(className)
                ? null
                : testCaseMapper.findByClassName(className).toDomain();
        } catch (Exception e) {
            LOGGER.error("findByClassName failed. {}", e);
            throw new IllegalRequestException(String.format(ErrorCode.DB_ERROR_MSG, "test case findByClassName failed"),
                ErrorCode.DB_ERROR, new ArrayList<String>(Arrays.asList("test case findByClassName failed")));
        }
    }
}
