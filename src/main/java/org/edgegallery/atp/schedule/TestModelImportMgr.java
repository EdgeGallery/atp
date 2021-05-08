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

    public Workbook getWorkbook(InputStream is) throws IOException {
        return new XSSFWorkbook(is);
    }

    /**
     * analysize test scenario info from sheet of excel.
     * 
     * @param wb Workbook
     * @return test scenario info list
     */
    public List<TestScenario> analysizeTestScenarioSheet(Workbook wb) {
        Sheet testScenarioSheet = wb.getSheet(Constant.TEST_SCENARIO);
        Iterator<Row> iter = testScenarioSheet.rowIterator();
        List<TestScenario> testScenarioImportList = new ArrayList<TestScenario>();
        // titile
        if (iter.hasNext()) {
            iter.next();
        }

        while (iter.hasNext()) {
            Row row = iter.next();
            // TODO 中英文一个，判断名字是否存在
            TestScenario testScenario = new TestScenario();
            testScenario.setNameCh(getCellValue(row, 0));
            testScenario.setNameEn(getCellValue(row, 1));
            testScenario.setDescriptionCh(getCellValue(row, 2));
            testScenario.setDescriptionEn(getCellValue(row, 3));
            testScenario.setId(CommonUtil.generateId());
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
            TestSuite testSuite = TestSuite.builder().setId(CommonUtil.generateId()).setNameCh(getCellValue(row, 0))
                    .setNameEn(getCellValue(row, 1)).setDescriptionCh(getCellValue(row, 2))
                    .setDescriptionEn(getCellValue(row, 3)).build();
            List<String> scenarioIdList = new ArrayList<String>();
            String scenarioNameList = getCellValue(row, 4);
            if (StringUtils.isNotBlank(scenarioNameList)) {
                String[] nameArray = scenarioNameList.split(Constant.COMMA);
                for (String scenarioName : nameArray) {
                    TestScenario testScenario = testScenarioRepository.getTestScenarioByName(null, scenarioName);
                    if (null == testScenario) {
                        LOGGER.error("test scenario name {} not exists.", scenarioName);
                        failures.add(CommonUtil.setFailureRes(testSuite.getId(), testSuite.getNameEn(),
                                Constant.TEST_SUITE, ErrorCode.TEST_SUITE_SCENARIO_NAME_NOT_EXISTS, null));
                        failureIds.add(testSuite.getId());
                    } else {
                        scenarioIdList.add(testScenario.getId());
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
            // TODO 中文的是不是就写一个就行了？
            TestCase testCase = TestCase.builder().setId(CommonUtil.generateId()).setNameCh(getCellValue(row, 0))
                    .setNameEn(getCellValue(row, 1)).setDescriptionCh(getCellValue(row, 2))
                    .setDescriptionEn(getCellValue(row, 3)).setType(getCellValue(row, 4))
                    .setCodeLanguage(getCellValue(row, 5)).setExpectResultCh(getCellValue(row, 6))
                    .setExpectResultEn(getCellValue(row, 7)).setTestStepCh(getCellValue(row, 8))
                    .setTestStepEn(getCellValue(row, 9)).build().toTestCase();
            List<String> suiteIdList = new ArrayList<String>();
            String suiteNameList = getCellValue(row, 10);
            if (StringUtils.isNotBlank(suiteNameList)) {
                String[] nameArray = suiteNameList.split(Constant.COMMA);
                for (String suiteName : nameArray) {
                    TestSuite testSuite = testSuiteRepository.getTestSuiteByName(null, suiteName);
                    if (null == testSuite) {
                        LOGGER.error("test suite name {} not exists.", suiteName);
                        failures.add(CommonUtil.setFailureRes(testCase.getId(), testCase.getNameEn(),
                                Constant.TEST_CASE, ErrorCode.TEST_CASE_TEST_SUITE_NAME_NOT_EXISTS, null));
                        failureIds.add(testCase.getId());
                    } else {
                        suiteIdList.add(testSuite.getId());
                    }
                }
            }
            testCase.setTestSuiteIdList(suiteIdList);
            testCaseImportList.add(testCase);
        }

        return testCaseImportList;
    }

    private String getCellValue(Row row, int index) {
        return null == row.getCell(index) ? null : row.getCell(index).getStringCellValue();
    }
}
