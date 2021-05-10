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

package org.edgegallery.atp.schedule;

import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.constant.ErrorCode;
import org.edgegallery.atp.model.testcase.TestCase;
import org.edgegallery.atp.model.testscenario.TestScenario;
import org.edgegallery.atp.model.testsuite.TestSuite;
import org.edgegallery.atp.repository.testcase.TestCaseRepository;
import org.edgegallery.atp.repository.testscenario.TestScenarioRepository;
import org.edgegallery.atp.repository.testsuite.TestSuiteRepository;
import org.edgegallery.atp.utils.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestModelImportMgr {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestModelImportMgr.class);

    @Autowired
    TestScenarioRepository testScenarioRepository;

    @Autowired
    TestSuiteRepository testSuiteRepository;

    @Autowired
    TestCaseRepository testCaseRepository;

    public Workbook getWorkbook(InputStream is) throws IOException {
        return new XSSFWorkbook(is);
    }

    /**
     * analysize test scenario info from sheet of excel.
     * 
     * @param wb Workbook
     * @return test scenario info list
     */
    public List<TestScenario> analysizeTestScenarioSheet(Workbook wb, List<JSONObject> failures,
            Set<String> failureIds) {
        Sheet testScenarioSheet = wb.getSheet(Constant.TEST_SCENARIO);
        Iterator<Row> iter = testScenarioSheet.rowIterator();
        List<TestScenario> testScenarioImportList = new ArrayList<TestScenario>();
        // titile
        if (iter.hasNext()) {
            iter.next();
        }

        while (iter.hasNext()) {
            Row row = iter.next();
            String nameCh = getCellValue(row, 0);
            String nameEn = getCellValue(row, 1);
            String descriptionCh = getCellValue(row, 2);
            String descriptionEn = getCellValue(row, 3);
            TestScenario testScenario = TestScenario.builder().setId(CommonUtil.generateId())
                    .setNameCh(CommonUtil.setParamOrDefault(nameCh, nameEn))
                    .setNameEn(CommonUtil.setParamOrDefault(nameEn, nameCh))
                    .setDescriptionCh(CommonUtil.setParamOrDefault(descriptionCh, descriptionEn))
                    .setDescriptionEn(CommonUtil.setParamOrDefault(descriptionEn, descriptionCh)).build();
            try {
                if (null != testScenarioRepository.getTestScenarioByName(nameCh, null)
                        || null != testScenarioRepository.getTestScenarioByName(null, nameEn)) {
                    LOGGER.error("name of test scenario {} or {} already exist.", nameCh, nameEn);
                    failures.add(CommonUtil.setFailureRes(testScenario.getId(), nameEn, Constant.TEST_SCENARIO,
                            ErrorCode.NAME_EXISTS, ErrorCode.NAME_EXISTS_MSG, Constant.TEST_SCENARIO.concat(nameEn)));
                    failureIds.add(testScenario.getId());
                }
            } catch (IllegalArgumentException e) {
                // db operate failed
                failures.add(CommonUtil.setFailureRes(testScenario.getId(), nameEn, Constant.TEST_SCENARIO,
                        ErrorCode.DB_ERROR, ErrorCode.DB_ERROR_MSG, Constant.TEST_SCENARIO));
                failureIds.add(testScenario.getId());
            }

            testScenarioImportList.add(testScenario);
        }

        return testScenarioImportList;
    }

    /**
     * analysize test suite info from sheet of excel.
     * 
     * @param wb workbook
     * @return test suite info list
     */
    public List<TestSuite> analysizeTestSuiteSheet(Workbook wb, List<JSONObject> failures, Set<String> failureIds) {
        Sheet testSuiteSheet = wb.getSheet(Constant.TEST_SUITE);
        Iterator<Row> iter = testSuiteSheet.rowIterator();
        // titile
        if (iter.hasNext()) {
            iter.next();
        }
        List<TestSuite> testSuiteImportList = new ArrayList<TestSuite>();

        while (iter.hasNext()) {
            Row row = iter.next();
            String nameCh = getCellValue(row, 0);
            String nameEn = getCellValue(row, 1);
            String descriptionCh = getCellValue(row, 2);
            String descriptionEn = getCellValue(row, 3);
            TestSuite testSuite = TestSuite.builder().setId(CommonUtil.generateId())
                    .setNameCh(CommonUtil.setParamOrDefault(nameCh, nameEn))
                    .setNameEn(CommonUtil.setParamOrDefault(nameEn, nameCh))
                    .setDescriptionCh(CommonUtil.setParamOrDefault(descriptionCh, descriptionEn))
                    .setDescriptionEn(CommonUtil.setParamOrDefault(descriptionEn, descriptionCh)).build();

            if (!testSuiteNameCheck(testSuite, failures, failureIds, testSuiteImportList)) {
                continue;
            }

            List<String> scenarioIdList = new ArrayList<String>();
            String scenarioNameList = getCellValue(row, 4);
            if (StringUtils.isNotBlank(scenarioNameList)) {
                String[] nameArray = scenarioNameList.split(Constant.COMMA);
                for (String scenarioName : nameArray) {
                    try {
                        TestScenario testScenario = testScenarioRepository.getTestScenarioByName(null, scenarioName);
                        if (null == testScenario) {
                            LOGGER.error("test scenario name {} not exists.", scenarioName);
                            failures.add(CommonUtil.setFailureRes(testSuite.getId(), nameEn, Constant.TEST_SUITE,
                                    ErrorCode.TEST_SUITE_SCENARIO_NAME_NOT_EXISTS,
                                    ErrorCode.TEST_SUITE_SCENARIO_NAME_NOT_EXISTS_MSG, null));
                            failureIds.add(testSuite.getId());
                        } else {
                            scenarioIdList.add(testScenario.getId());
                        }
                    } catch (IllegalArgumentException e) {
                        // db error
                        failures.add(CommonUtil.setFailureRes(testSuite.getId(), nameEn, Constant.TEST_SUITE,
                                ErrorCode.DB_ERROR, ErrorCode.DB_ERROR_MSG, Constant.TEST_SUITE));
                        failureIds.add(testSuite.getId());
                    }
                }
            }
            testSuite.setScenarioIdList(scenarioIdList);
            testSuiteImportList.add(testSuite);
        }
        return testSuiteImportList;
    }

    /**
     * analysize test case info from sheet of excel.
     * 
     * @param wb workbook
     * @return test case info list
     */
    public List<TestCase> analysizeTestCaseSheet(Workbook wb, List<JSONObject> failures, Set<String> failureIds) {
        Sheet testCaseSheet = wb.getSheet(Constant.TEST_CASE);
        Iterator<Row> iter = testCaseSheet.rowIterator();
        // titile
        if (iter.hasNext()) {
            iter.next();
        }
        List<TestCase> testCaseImportList = new ArrayList<TestCase>();

        while (iter.hasNext()) {
            Row row = iter.next();
            String nameCh = getCellValue(row, 0);
            String nameEn = getCellValue(row, 1);
            String descriptionCh = getCellValue(row, 2);
            String descriptionEn = getCellValue(row, 3);
            String expectResultCh = getCellValue(row, 6);
            String expectResultEn = getCellValue(row, 7);
            String testStepCh = getCellValue(row, 8);
            String testSepEn = getCellValue(row, 9);
            TestCase testCase = TestCase.builder().setId(CommonUtil.generateId())
                    .setNameCh(CommonUtil.setParamOrDefault(nameCh, nameEn))
                    .setNameEn(CommonUtil.setParamOrDefault(nameEn, nameCh))
                    .setDescriptionCh(CommonUtil.setParamOrDefault(descriptionCh, descriptionEn))
                    .setDescriptionEn(CommonUtil.setParamOrDefault(descriptionEn, descriptionCh))
                    .setType(getCellValue(row, 4)).setCodeLanguage(getCellValue(row, 5))
                    .setExpectResultCh(CommonUtil.setParamOrDefault(expectResultCh, expectResultEn))
                    .setExpectResultEn(CommonUtil.setParamOrDefault(expectResultEn, expectResultCh))
                    .setTestStepCh(CommonUtil.setParamOrDefault(testStepCh, testSepEn))
                    .setTestStepEn(CommonUtil.setParamOrDefault(testSepEn, testStepCh)).build().toTestCase();

            if (!testCaseNameCheck(testCase, failures, failureIds, testCaseImportList)) {
                continue;
            }

            List<String> suiteIdList = new ArrayList<String>();
            String suiteNameList = getCellValue(row, 10);
            if (StringUtils.isNotBlank(suiteNameList)) {
                String[] nameArray = suiteNameList.split(Constant.COMMA);
                for (String suiteName : nameArray) {
                    try {
                        TestSuite testSuite = testSuiteRepository.getTestSuiteByName(null, suiteName);
                        if (null == testSuite) {
                            LOGGER.error("test suite name {} not exists.", suiteName);
                            failures.add(CommonUtil.setFailureRes(testCase.getId(), testCase.getNameEn(),
                                    Constant.TEST_CASE, ErrorCode.TEST_CASE_TEST_SUITE_NAME_NOT_EXISTS,
                                    ErrorCode.TEST_CASE_TEST_SUITE_NAME_NOT_EXISTS_MSG, null));
                            failureIds.add(testCase.getId());
                        } else {
                            suiteIdList.add(testSuite.getId());
                        }
                    } catch (IllegalArgumentException e) {
                        failures.add(CommonUtil.setFailureRes(testCase.getId(), testCase.getNameEn(),
                                Constant.TEST_CASE, ErrorCode.DB_ERROR, ErrorCode.DB_ERROR_MSG, Constant.TEST_CASE));
                        failureIds.add(testCase.getId());
                    }
                }
            }
            testCase.setTestSuiteIdList(suiteIdList);
            testCaseImportList.add(testCase);
        }

        return testCaseImportList;
    }

    /**
     * get index th value in excel.
     * 
     * @param row row
     * @param index index
     * @return value
     */
    private String getCellValue(Row row, int index) {
        return null == row.getCell(index) ? null : row.getCell(index).getStringCellValue();
    }

    /**
     * test suite name exists check successfully.
     * 
     * @param testSuite testSuite
     * @param failures failures
     * @param failureIds failureIds
     * @param testSuiteImportList testSuiteImportList
     * @return is checking successfully.
     */
    private boolean testSuiteNameCheck(TestSuite testSuite, List<JSONObject> failures, Set<String> failureIds,
            List<TestSuite> testSuiteImportList) {
        try {
            if (null != testSuiteRepository.getTestSuiteByName(testSuite.getNameCh(), null)
                    || null != testSuiteRepository.getTestSuiteByName(null, testSuite.getNameEn())) {
                LOGGER.error("name of test suite {} or {} already exist.", testSuite.getNameCh(),
                        testSuite.getNameEn());
                failures.add(CommonUtil.setFailureRes(testSuite.getId(), testSuite.getNameEn(), Constant.TEST_SUITE,
                        ErrorCode.NAME_EXISTS, ErrorCode.NAME_EXISTS_MSG,
                        Constant.TEST_SUITE.concat(testSuite.getNameEn())));
                failureIds.add(testSuite.getId());
                testSuiteImportList.add(testSuite);
                return false;
            }
        } catch (IllegalArgumentException e) {
            failures.add(CommonUtil.setFailureRes(testSuite.getId(), testSuite.getNameEn(), Constant.TEST_SUITE,
                    ErrorCode.DB_ERROR, ErrorCode.DB_ERROR_MSG, Constant.TEST_SUITE));
            failureIds.add(testSuite.getId());
            testSuiteImportList.add(testSuite);
            return false;
        }
        return true;
    }

    /**
     * test case name exists check successfully.
     * 
     * @param testCase testCase
     * @param failures failures
     * @param failureIds failureIds
     * @param testCaseImportList testCaseImportList
     * @return is checking successfully.
     */
    private boolean testCaseNameCheck(TestCase testCase, List<JSONObject> failures, Set<String> failureIds,
            List<TestCase> testCaseImportList) {
        try {
            if (null != testCaseRepository.findByName(testCase.getNameCh(), null)
                    || null != testCaseRepository.findByName(null, testCase.getNameEn())) {
                LOGGER.error("name of test case {} or {} already exist.", testCase.getNameCh(), testCase.getNameEn());
                failures.add(CommonUtil.setFailureRes(testCase.getId(), testCase.getNameEn(), Constant.TEST_CASE,
                        ErrorCode.NAME_EXISTS, ErrorCode.NAME_EXISTS_MSG,
                        Constant.TEST_CASE.concat(testCase.getNameEn())));
                failureIds.add(testCase.getId());
                testCaseImportList.add(testCase);
                return false;
            }
        } catch (IllegalArgumentException e) {
            failures.add(CommonUtil.setFailureRes(testCase.getId(), testCase.getNameEn(), Constant.TEST_CASE,
                    ErrorCode.DB_ERROR, ErrorCode.DB_ERROR_MSG, Constant.TEST_CASE));
            failureIds.add(testCase.getId());
            testCaseImportList.add(testCase);
            return false;
        }
        return true;
    }
}
