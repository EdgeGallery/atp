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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.model.testcase.TestCase;
import org.edgegallery.atp.model.testsuite.TestSuite;
import org.edgegallery.atp.repository.testcase.TestCaseRepository;
import org.edgegallery.atp.repository.testsuite.TestSuiteRepository;
import org.edgegallery.atp.utils.CommonUtil;
import org.edgegallery.atp.utils.FileChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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

    @Override
    public ResponseEntity<List<TestCase>> getAllTestCases(String type, String locale, String name,
            List<String> testSuiteIds) {
        List<TestCase> result = new LinkedList<TestCase>();
        if (null == testSuiteIds || CollectionUtils.isEmpty(testSuiteIds)) {
            result = testCaseRepository.findAllTestCases(type, locale, name, null);
        } else {
            List<TestCase> testCaseList = testCaseRepository.findAllTestCases(type, locale, name, testSuiteIds.get(0));
            for (TestCase testCase : testCaseList) {
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
            }
        }
        LOGGER.info("query all test cases successfully.");
        return ResponseEntity.ok(result);
    }

    @Override
    public TestCase createTestCase(MultipartFile file, TestCase testCase) {
        // nameCh or nameEn must exist one
        testCase.setNameCh(StringUtils.isNotBlank(testCase.getNameCh()) ? testCase.getNameCh() : testCase.getNameEn());
        testCase.setNameEn(StringUtils.isNotBlank(testCase.getNameEn()) ? testCase.getNameEn() : testCase.getNameCh());
        if (StringUtils.isEmpty(testCase.getNameCh()) && StringUtils.isEmpty(testCase.getNameEn())) {
            String msg = "both nameCh and nameEn is empty";
            LOGGER.error(msg);
            throw new IllegalArgumentException(msg);
        }
        if (null != testCaseRepository.findByName(testCase.getNameCh(), null)
                || null != testCaseRepository.findByName(null, testCase.getNameEn())) {
            String msg = "name of test case already exist.";
            LOGGER.error(msg);
            throw new IllegalArgumentException(msg);
        }
        checkTestSuiteIdsExist(testCase);

        testCase.setDescriptionCh(StringUtils.isNotBlank(testCase.getDescriptionCh()) ? testCase.getDescriptionCh()
                : testCase.getDescriptionEn());
        testCase.setDescriptionEn(StringUtils.isNotBlank(testCase.getDescriptionEn()) ? testCase.getDescriptionEn()
                : testCase.getDescriptionCh());
        testCase.setExpectResultCh(StringUtils.isNotBlank(testCase.getExpectResultCh()) ? testCase.getExpectResultCh()
                : testCase.getExpectResultEn());
        testCase.setExpectResultEn(StringUtils.isNotBlank(testCase.getExpectResultEn()) ? testCase.getExpectResultEn()
                : testCase.getExpectResultCh());
        testCase.setTestStepCh(
                StringUtils.isNotBlank(testCase.getTestStepCh()) ? testCase.getTestStepCh() : testCase.getTestStepEn());
        testCase.setTestStepEn(
                StringUtils.isNotBlank(testCase.getTestStepEn()) ? testCase.getTestStepEn() : testCase.getTestStepCh());

        // check one test case type must same in one test suite
        testCase.getTestSuiteIdList().forEach(testSuiteId -> {
            List<TestCase> testCaseList = testCaseRepository.findAllTestCases(null, null, null, testSuiteId);
            testCaseList.forEach(testCaseDb -> {
                if (!testCaseDb.getType().equals(testCase.getType())) {
                    String msg = "test case type in testSuiteIds is not the same as others.";
                    LOGGER.error(msg);
                    throw new IllegalArgumentException(msg);
                }
            });
        });

        String filePath = Constant.BASIC_TEST_CASE_PATH.concat(testCase.getNameEn()).concat(Constant.UNDER_LINE)
                .concat(testCase.getId());
        try {
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
            throw new IllegalArgumentException("create file failed.");
        }
        LOGGER.info("create test case successfully.");
        return testCase;
    }

    @Override
    public TestCase updateTestCase(MultipartFile file, TestCase testCase) {
        TestCase dbData = testCaseRepository.getTestCaseById(testCase.getId());
        if (null == dbData) {
            LOGGER.error("this test case {} not exists.", testCase.getId());
            throw new IllegalArgumentException("this test case not exists.");
        }

        try {
            if (null != file && StringUtils.isNotBlank(file.getOriginalFilename())
                    && StringUtils.isNotBlank(file.getName()) && 0 != (int) file.getSize()) {
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
            throw new IllegalArgumentException("update file failed.");
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
    public TestCase getTestCase(String id) throws FileNotFoundException {
        TestCase response = testCaseRepository.getTestCaseById(id);
        if (null == response) {
            LOGGER.error("test case id does not exists: {}", id);
            throw new FileNotFoundException("test case id does not exists.");
        }
        LOGGER.info("get test case successfully.");
        return response;
    }


    @Override
    public ResponseEntity<InputStreamResource> downloadTestCase(String id) {
        TestCase testCase = testCaseRepository.getTestCaseById(id);
        if (null == testCase) {
            String msg = "test case not exists.";
            LOGGER.error(msg);
            throw new IllegalArgumentException(msg);
        }
        File file = new File(testCase.getFilePath());
        try {
            InputStream fileContent = new FileInputStream(file);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/octet-stream");
            LOGGER.info("download test case successfully.");
            return new ResponseEntity<>(new InputStreamResource(fileContent), headers, HttpStatus.OK);
        } catch (FileNotFoundException e) {
            String msg = "file not exists.";
            LOGGER.error(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * check test suite ids is right.
     * 
     * @param testSuite test suite info
     */
    private void checkTestSuiteIdsExist(TestCase testCase) {
        List<TestSuite> testSuiteList = testSuiteRepository.batchQueryTestSuites(testCase.getTestSuiteIdList());
        if (testSuiteList.size() != testCase.getTestSuiteIdList().size()) {
            String msg = "some test suite ids do not exist.";
            LOGGER.error(msg);
            throw new IllegalArgumentException(msg);
        }
    }
}
