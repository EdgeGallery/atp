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

package org.edgegallery.atp.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.constant.ErrorCode;
import org.edgegallery.atp.model.PageResult;
import org.edgegallery.atp.model.config.Config;
import org.edgegallery.atp.model.testcase.TestCase;
import org.edgegallery.atp.model.testsuite.TestSuite;
import org.edgegallery.atp.repository.config.ConfigRepository;
import org.edgegallery.atp.repository.task.TaskRepository;
import org.edgegallery.atp.repository.testcase.TestCaseRepository;
import org.edgegallery.atp.repository.testsuite.TestSuiteRepository;
import org.edgegallery.atp.utils.CommonUtil;
import org.edgegallery.atp.utils.FileChecker;
import org.edgegallery.atp.utils.exception.FileNotExistsException;
import org.edgegallery.atp.utils.exception.IllegalRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

@Service("TestCaseService")
public class TestCaseServiceImpl implements TestCaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestCaseServiceImpl.class);

    @Autowired
    TestCaseRepository testCaseRepository;

    @Autowired
    TestSuiteRepository testSuiteRepository;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    ConfigRepository configRepository;

    @Override
    public List<TestCase> getAllTestCases(String type, String locale, String name, List<String> testSuiteIds) {
        List<TestCase> result = new LinkedList<TestCase>();
        if (CollectionUtils.isEmpty(testSuiteIds)) {
            result = testCaseRepository.findAllTestCases(type, locale, name, null);
        } else {
            List<TestCase> testCaseList = testCaseRepository.findAllTestCases(type, locale, name, testSuiteIds.get(0));
            setQueryResult(testCaseList, testSuiteIds, result);
        }
        LOGGER.info("query all test cases successfully.");
        return result;
    }

    @Override
    public PageResult<TestCase> getAllTestCasesByPagination(String type, String locale, String name,
        List<String> testSuiteIds, int limit, int offset) {
        List<TestCase> result = new LinkedList<TestCase>();
        PageResult<TestCase> pageResult = new PageResult<TestCase>(offset, limit);
        if (CollectionUtils.isEmpty(testSuiteIds)) {
            result = testCaseRepository.findAllTestCasesByPaginition(type, locale, name, null, limit, offset);
            pageResult.setTotal(testCaseRepository.countTotal(type, locale, name, null));
        } else {
            pageResult.setTotal(getAllTestCases(type, locale, name, testSuiteIds).size());
            List<TestCase> testCaseList = testCaseRepository
                .findAllTestCasesByPaginition(type, locale, name, testSuiteIds.get(0), limit, offset);
            setQueryResult(testCaseList, testSuiteIds, result);
        }

        pageResult.setResults(result);
        LOGGER.info("query all test cases by pagination successfully.");
        return pageResult;
    }

    @Override
    public TestCase createTestCase(MultipartFile file, TestCase testCase) {
        CommonUtil.nameNotEmptyValidation(testCase.getNameCh(), testCase.getNameEn());
        constructTestCase(testCase);
        checkNameExistence(testCase);
        checkTestSuiteIdsExist(testCase);
        checkConfigIdsExist(testCase.getConfigIdList());
        // check one test case type must same in one test suite
        checkTestCaseTypeConsistence(testCase);
        try {
            String filePath = Constant.BASIC_TEST_CASE_PATH.concat(testCase.getNameEn()).concat(Constant.UNDER_LINE)
                .concat(testCase.getId());
            FileChecker.createFile(filePath);
            File result = new File(filePath);
            file.transferTo(result);
            testCase.setFilePath(filePath);

            if (Constant.JAVA.equals(testCase.getCodeLanguage())) {
                testCase.setClassName(CommonUtil.getClassPath(result));
            }
            testCaseRepository.insert(testCase);
        } catch (IOException e) {
            LOGGER.error("create file failed, test case name is: {}", testCase.getNameEn());
            throw new IllegalRequestException(ErrorCode.FILE_IO_EXCEPTION_MSG, ErrorCode.FILE_IO_EXCEPTION, null);
        }
        LOGGER.info("create test case successfully.");
        return testCase;
    }

    @Override
    public TestCase updateTestCase(MultipartFile file, TestCase testCase) throws FileNotExistsException {
        TestCase dbData = testCaseRepository.getTestCaseById(testCase.getId());
        checkTestCaseExistence(dbData, testCase.getId());
        try {
            if (checkFileNotEmpty(file)) {
                String filePath = dbData.getFilePath();
                CommonUtil.deleteFile(filePath);
                File result = new File(filePath);
                file.transferTo(result);
                if (Constant.JAVA.equals(testCase.getCodeLanguage())) {
                    testCase.setClassName(CommonUtil.getClassPath(result));
                }
            }
            testCaseRepository.update(testCase);
        } catch (IOException e) {
            LOGGER.error("transfer file content failed.{}", e.getMessage());
            throw new IllegalRequestException(ErrorCode.FILE_IO_EXCEPTION_MSG, ErrorCode.FILE_IO_EXCEPTION, null);
        }
        return testCaseRepository.getTestCaseById(testCase.getId());
    }

    @Override
    public Boolean deleteTestCase(String id) {
        TestCase testCase = testCaseRepository.getTestCaseById(id);
        if (null != testCase) {
            String filePath = testCase.getFilePath();
            testCaseRepository.delete(id);
            CommonUtil.deleteFile(filePath);
        }
        return true;
    }

    @Override
    public TestCase getTestCase(String id) throws FileNotExistsException {
        TestCase testCase = testCaseRepository.getTestCaseById(id);
        checkTestCaseExistence(testCase, id);
        LOGGER.info("get test case successfully.");
        return testCase;
    }

    @Override
    public ResponseEntity<byte[]> downloadTestCase(String id) {
        TestCase testCase = testCaseRepository.getTestCaseById(id);
        CommonUtil.checkParamEmpty(testCase, "test case not exists.");
        try {
            File file = new File(testCase.getFilePath());
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/octet-stream");
            headers.add("Content-Disposition", "attachment; filename=" + testCase.getNameEn());
            byte[] fileData = FileUtils.readFileToByteArray(file);
            LOGGER.info("download test case successfully.");
            return ResponseEntity.ok().headers(headers).body(fileData);
        } catch (IOException e) {
            String msg = "download test case failed.";
            LOGGER.error(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    private void checkTestSuiteIdsExist(TestCase testCase) {
        List<TestSuite> testSuiteList = testSuiteRepository.batchQueryTestSuites(testCase.getTestSuiteIdList());
        if (testSuiteList.size() != testCase.getTestSuiteIdList().size()) {
            LOGGER.error("some test suite ids do not exist.");
            throw new IllegalRequestException(String.format(ErrorCode.NOT_FOUND_EXCEPTION_MSG, "some test suite ids."),
                ErrorCode.NOT_FOUND_EXCEPTION, new ArrayList<String>(Arrays.asList("some test suite ids.")));
        }
    }

    private void checkConfigIdsExist(List<String> configIds) {
        if (!CollectionUtils.isEmpty(configIds)) {
            configIds.forEach(id -> {
                Config config = configRepository.queryConfigById(id);
                if (null == config) {
                    LOGGER.error("config id {} does not exist", id);
                    throw new IllegalRequestException(
                        String.format(ErrorCode.NOT_FOUND_EXCEPTION_MSG, "config id: ".concat(id)),
                        ErrorCode.NOT_FOUND_EXCEPTION, new ArrayList<String>(Arrays.asList("config id: ".concat(id))));
                }
            });
        }
    }

    private void setQueryResult(List<TestCase> testCaseList, List<String> testSuiteIds, List<TestCase> result) {
        testCaseList.forEach(testCase -> {
            boolean isSatisfy = true;
            for (String id : testSuiteIds) {
                if (!testCase.getTestSuiteIdList().contains(id)) {
                    isSatisfy = false;
                    break;
                }
            }
            if (isSatisfy) {
                result.add(testCase);
            }
        });
    }

    private void checkTestCaseTypeConsistence(TestCase testCase) {
        testCase.getTestSuiteIdList().forEach(testSuiteId -> {
            List<TestCase> testCaseList = testCaseRepository.findAllTestCases(null, null, null, testSuiteId);
            testCaseList.forEach(testCaseDb -> {
                if (!testCaseDb.getType().equals(testCase.getType())) {
                    LOGGER.error("test case type in testSuiteIds is not the same as others.");
                    throw new IllegalRequestException(ErrorCode.TEST_CASE_TYPE_COMPATIBILITY_ERROR_MSG,
                        ErrorCode.TEST_CASE_TYPE_COMPATIBILITY_ERROR, null);
                }
            });
        });
    }

    private void checkNameExistence(TestCase testCase) {
        if (null != testCaseRepository.findByName(testCase.getNameCh(), null) || null != testCaseRepository
            .findByName(null, testCase.getNameEn())) {
            LOGGER.error("name of test case already exist.");
            String param = testCase.getNameCh() + " or " + testCase.getNameEn();
            throw new IllegalRequestException(String.format(ErrorCode.NAME_EXISTS_MSG, param), ErrorCode.NAME_EXISTS,
                new ArrayList<String>(Arrays.asList(param)));
        }
    }

    private void constructTestCase(TestCase testCase) {
        testCase.setNameCh(StringUtils.isNotBlank(testCase.getNameCh()) ? testCase.getNameCh() : testCase.getNameEn());
        testCase.setNameEn(StringUtils.isNotBlank(testCase.getNameEn()) ? testCase.getNameEn() : testCase.getNameCh());
        testCase.setDescriptionCh(StringUtils.isNotBlank(testCase.getDescriptionCh())
            ? testCase.getDescriptionCh()
            : testCase.getDescriptionEn());
        testCase.setDescriptionEn(StringUtils.isNotBlank(testCase.getDescriptionEn())
            ? testCase.getDescriptionEn()
            : testCase.getDescriptionCh());
        testCase.setExpectResultCh(StringUtils.isNotBlank(testCase.getExpectResultCh())
            ? testCase.getExpectResultCh()
            : testCase.getExpectResultEn());
        testCase.setExpectResultEn(StringUtils.isNotBlank(testCase.getExpectResultEn())
            ? testCase.getExpectResultEn()
            : testCase.getExpectResultCh());
        testCase.setTestStepCh(
            StringUtils.isNotBlank(testCase.getTestStepCh()) ? testCase.getTestStepCh() : testCase.getTestStepEn());
        testCase.setTestStepEn(
            StringUtils.isNotBlank(testCase.getTestStepEn()) ? testCase.getTestStepEn() : testCase.getTestStepCh());
        testCase.setCreateTime(taskRepository.getCurrentDate());
    }

    private boolean checkFileNotEmpty(MultipartFile file) {
        return null != file && StringUtils.isNotBlank(file.getOriginalFilename()) && StringUtils
            .isNotBlank(file.getName()) && 0 != (int) file.getSize();
    }

    private void checkTestCaseExistence(TestCase testCase, String testCaseId) throws FileNotExistsException {
        if (null == testCase) {
            LOGGER.error("this test case {} not exists in db.", testCaseId);
            throw new FileNotExistsException(String.format(ErrorCode.NOT_FOUND_EXCEPTION_MSG, Constant.TEST_CASE_ID),
                ErrorCode.NOT_FOUND_EXCEPTION, new ArrayList<String>(Arrays.asList(Constant.TEST_CASE_ID)));
        }
    }
}
