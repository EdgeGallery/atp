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

package org.edgegallery.atp.service;

import com.alibaba.fastjson.JSONObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.constant.ErrorCode;
import org.edgegallery.atp.model.BatchOpsRes;
import org.edgegallery.atp.model.file.AtpFile;
import org.edgegallery.atp.model.testcase.TestCase;
import org.edgegallery.atp.model.testscenario.TestScenario;
import org.edgegallery.atp.model.testscenario.testcase.AllTestScenarios;
import org.edgegallery.atp.model.testscenario.testcase.AllTestSuites;
import org.edgegallery.atp.model.testsuite.TestSuite;
import org.edgegallery.atp.repository.file.FileRepository;
import org.edgegallery.atp.repository.task.TaskRepository;
import org.edgegallery.atp.repository.testcase.TestCaseRepository;
import org.edgegallery.atp.repository.testscenario.TestScenarioRepository;
import org.edgegallery.atp.repository.testsuite.TestSuiteRepository;
import org.edgegallery.atp.schedule.TestModelImportMgr;
import org.edgegallery.atp.utils.CommonUtil;
import org.edgegallery.atp.utils.FileChecker;
import org.edgegallery.atp.utils.exception.FileNotExistsException;
import org.edgegallery.atp.utils.exception.IllegalRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service("TestScenarioService")
public class TestScenarioServiceImpl implements TestScenarioService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestScenarioServiceImpl.class);

    private static final String CREATE_TEST_CASE_FAILED = "create test case failed";

    private static final String TEST_CASE = "testCase";

    private static final String TEST_SCENARIO_ICON = "testScenarioIcon";

    @Autowired
    TestScenarioRepository testScenarioRepository;

    @Autowired
    FileRepository fileRepository;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    TestCaseRepository testCaseRepository;

    @Autowired
    TestSuiteRepository testSuiteRepository;

    @Autowired
    TestModelImportMgr importMgr;

    @Override
    public TestScenario createTestScenario(TestScenario testScenario, MultipartFile icon) {
        // nameCh or nameEn must exist one
        testScenario.setNameCh(
            StringUtils.isNotBlank(testScenario.getNameCh()) ? testScenario.getNameCh() : testScenario.getNameEn());
        testScenario.setNameEn(
            StringUtils.isNotBlank(testScenario.getNameEn()) ? testScenario.getNameEn() : testScenario.getNameCh());
        if (StringUtils.isEmpty(testScenario.getNameCh()) && StringUtils.isEmpty(testScenario.getNameEn())) {
            LOGGER.error("both nameCh and nameEn is empty.");
            throw new IllegalRequestException(String.format(ErrorCode.PARAM_IS_NULL_MSG, "nameCh and nameEn both"),
                ErrorCode.PARAM_IS_NULL, new ArrayList<String>(Arrays.asList("nameCh and nameEn both")));
        }
        testScenario.setDescriptionCh(StringUtils.isNotBlank(testScenario.getDescriptionCh())
            ? testScenario.getDescriptionCh()
            : testScenario.getDescriptionEn());
        testScenario.setDescriptionEn(StringUtils.isNotBlank(testScenario.getDescriptionEn())
            ? testScenario.getDescriptionEn()
            : testScenario.getDescriptionCh());
        testScenario.setCreateTime(taskRepository.getCurrentDate());

        checkNameExists(testScenario);
        if (null == icon) {
            LOGGER.error("icon file is empty.");
            throw new IllegalRequestException(String.format(ErrorCode.PARAM_IS_NULL_MSG, "icon file"),
                ErrorCode.PARAM_IS_NULL, new ArrayList<String>(Arrays.asList("icon file")));
        }
        String iconName = icon.getOriginalFilename();
        if (null == iconName) {
            LOGGER.error("icon file name is empty.");
            throw new IllegalRequestException(String.format(ErrorCode.PARAM_IS_NULL_MSG, "icon file name"),
                ErrorCode.PARAM_IS_NULL, new ArrayList<String>(Arrays.asList("icon file Name")));
        }
        String suffix = iconName.substring(iconName.indexOf(Constant.DOT) + 1);
        String filePath = Constant.BASIC_ICON_PATH.concat(Constant.FILE_TYPE_SCENARIO).concat(Constant.UNDER_LINE)
            .concat(testScenario.getId()).concat(Constant.DOT).concat(suffix);
        FileChecker.copyMultiFileToDir(icon, filePath);
        AtpFile atpFile = new AtpFile(testScenario.getId(), Constant.FILE_TYPE_SCENARIO,
            taskRepository.getCurrentDate(), filePath);
        fileRepository.insertFile(atpFile);
        testScenarioRepository.createTestScenario(testScenario);
        LOGGER.info("create test scenario successfully.");
        return testScenario;
    }

    @Override
    public TestScenario updateTestScenario(TestScenario testScenario, MultipartFile icon) {
        TestScenario dbData = testScenarioRepository.getTestScenarioById(testScenario.getId());
        if (!dbData.getNameCh().equalsIgnoreCase(testScenario.getNameCh()) && null != testScenarioRepository
            .getTestScenarioByName(testScenario.getNameCh(), null)) {
            LOGGER.error("chinese name of test scenario already exist.");
            throw new IllegalRequestException(String.format(ErrorCode.NAME_EXISTS_MSG, testScenario.getNameCh()),
                ErrorCode.NAME_EXISTS, new ArrayList<String>(Arrays.asList(testScenario.getNameCh())));
        }
        if (!dbData.getNameEn().equalsIgnoreCase(testScenario.getNameEn()) && null != testScenarioRepository
            .getTestScenarioByName(null, testScenario.getNameEn())) {
            LOGGER.error("english name of test suite already exist.");
            throw new IllegalRequestException(String.format(ErrorCode.NAME_EXISTS_MSG, testScenario.getNameEn()),
                ErrorCode.NAME_EXISTS, new ArrayList<String>(Arrays.asList(testScenario.getNameEn())));
        }
        testScenarioRepository.updateTestScenario(testScenario);

        if (null != icon && StringUtils.isNotBlank(icon.getOriginalFilename()) && StringUtils.isNotBlank(icon.getName())
            && 0 != (int) icon.getSize()) {
            try {
                AtpFile file = fileRepository.getFileContent(testScenario.getId(), Constant.FILE_TYPE_SCENARIO);
                String filePath = file.getFilePath();
                CommonUtil.deleteFile(filePath);
                File result = new File(filePath);
                icon.transferTo(result);
            } catch (IOException e) {
                LOGGER.error("file store failed, {}", e);
            }
        }
        LOGGER.info("update test scenario successfully.");
        return testScenarioRepository.getTestScenarioById(testScenario.getId());
    }

    @Override
    public Boolean deleteTestScenario(String id) {
        List<TestSuite> testSuiteList = testSuiteRepository.getAllTestSuites(null, null, id);
        if (CollectionUtils.isNotEmpty(testSuiteList)) {
            LOGGER.error("scenario id {} is used by some test suites, so can not be delete.", id);
            throw new IllegalArgumentException("this scenario is used by some test suites, so can not be delete.");
        }
        AtpFile file = fileRepository.getFileContent(id, Constant.FILE_TYPE_SCENARIO);
        CommonUtil.deleteFile(file.getFilePath());
        testScenarioRepository.deleteTestScenario(id);
        LOGGER.info("delete test scenario successfully.");
        return true;
    }

    @Override
    public TestScenario getTestScenario(String id) throws FileNotFoundException {
        TestScenario result = testScenarioRepository.getTestScenarioById(id);
        if (null == result) {
            LOGGER.error("test scenario id does not exists: {}", id);
            throw new FileNotExistsException(String.format(ErrorCode.NOT_FOUND_EXCEPTION_MSG, "test scenario id"),
                ErrorCode.NOT_FOUND_EXCEPTION, new ArrayList<String>(Arrays.asList("test scenario id")));
        }
        LOGGER.info("get test scenario by id successfully.");
        return result;
    }

    @Override
    public List<TestScenario> queryAllTestScenario(String locale, String name) {
        List<TestScenario> testScenarioList = testScenarioRepository.getAllTestScenarios(locale, name);
        LOGGER.info("get all test scenarios successfully.");
        return testScenarioList;
    }

    @Override
    public List<AllTestScenarios> getTestCasesByScenarioIds(List<String> ids) {
        List<AllTestScenarios> result = new ArrayList<AllTestScenarios>();
        ids.forEach(scenarioId -> {
            TestScenario testScenario = testScenarioRepository.getTestScenarioById(scenarioId);
            if (null == testScenario) {
                LOGGER.error("scenarioId {} not exists", scenarioId);
                throw new IllegalArgumentException("scenarioId not exists.");
            }

            AllTestScenarios allTestScenarios = new AllTestScenarios(testScenario);
            List<AllTestSuites> testSuites = new ArrayList<AllTestSuites>();

            List<TestSuite> testSuiteList = testSuiteRepository.getAllTestSuites(null, null, scenarioId);
            testSuiteList.forEach(testSuite -> {
                AllTestSuites allTestSuite = new AllTestSuites(testSuite);
                List<TestCase> testCaseList = testCaseRepository.findAllTestCases(null, null, null, testSuite.getId());
                allTestSuite.setTestCases(testCaseList);
                testSuites.add(allTestSuite);
            });
            allTestScenarios.setTestSuites(testSuites);
            result.add(allTestScenarios);
        });
        return result;
    }

    @Override
    public BatchOpsRes importTestModels(MultipartFile file) {
        String filePath = Constant.TEMP_FILE_PATH.concat(CommonUtil.generateId() + file.getOriginalFilename());
        try {
            FileChecker.copyMultiFileToDir(file, filePath);
            FileChecker.unzip(filePath);
        } catch (IOException e) {
            LOGGER.error("import test models bomb defense failed. {}", e);
            CommonUtil.deleteFile(filePath);
            throw new IllegalRequestException(ErrorCode.BOMB_DEFENSE_FAILED_MSG, ErrorCode.BOMB_DEFENSE_FAILED, null);
        }

        List<TestScenario> testScenarioList = new ArrayList<TestScenario>();
        List<TestSuite> testSuiteList = new ArrayList<TestSuite>();
        List<TestCase> testCaseList = new ArrayList<TestCase>();
        // key: file name, value: file
        Map<String, File> testCaseFile = new HashMap<String, File>();
        Map<String, File> scenarioIconFile = new HashMap<String, File>();
        List<JSONObject> failures = new ArrayList<JSONObject>();
        Set<String> failureIds = new HashSet<String>();

        try (ZipFile zipFile = new ZipFile(filePath)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String entryName = entry.getName();
                // analysize excel file
                if (entryName.endsWith(".xlsx") || entryName.endsWith(".xls")) {
                    InputStream inputStream = zipFile.getInputStream(entry);
                    Workbook workbook = importMgr.getWorkbook(inputStream);
                    importMgr.dataNumCheck(workbook);

                    testScenarioList = importMgr.analysizeTestScenarioSheet(workbook, failures, failureIds);
                    // insert db first, because in analysizeTestSuiteSheet needs to query test scenario
                    saveScenario2DB(testScenarioList, failures, failureIds);
                    testSuiteList = importMgr.analysizeTestSuiteSheet(workbook, failures, failureIds);
                    saveTestSuite2DB(testSuiteList, failures, failureIds);
                    testCaseList = importMgr.analysizeTestCaseSheet(workbook, failures, failureIds);
                    saveTestCase2DB(testCaseList, failures, failureIds);
                }

                String[] splitBySlash = entry.getName().split(Constant.SLASH);
                // storage test case file
                if (splitBySlash.length == 2 && TEST_CASE.equalsIgnoreCase(splitBySlash[0].trim())) {
                    String name = splitBySlash[1].trim();
                    String nameWithoutSuffix = name.substring(0, name.indexOf(Constant.DOT));
                    File targetFile = new File(Constant.TEMP_FILE_PATH.concat(nameWithoutSuffix));
                    FileUtils.copyInputStreamToFile(zipFile.getInputStream(entry), targetFile);
                    testCaseFile.put(nameWithoutSuffix, targetFile);
                }

                // storage test scenario icon
                if (splitBySlash.length == 2 && TEST_SCENARIO_ICON.equalsIgnoreCase(splitBySlash[0].trim())) {
                    String name = splitBySlash[1].trim();
                    String nameWithoutSuffix = name.substring(0, name.indexOf(Constant.DOT));
                    File targetFile = new File(Constant.TEMP_FILE_PATH.concat(nameWithoutSuffix));
                    FileUtils.copyInputStreamToFile(zipFile.getInputStream(entry), targetFile);
                    scenarioIconFile.put(nameWithoutSuffix, targetFile);
                }
            }
        } catch (IOException e) {
            LOGGER.error("import test models analysize zip package failed. {}", e);
            CommonUtil.deleteFile(filePath);
            throw new IllegalArgumentException("import test models analysize zip package failed.");
        }

        // update file info to db
        updateScenarioAndIconFile(testScenarioList, scenarioIconFile, failures, failureIds);
        updateTestCaseFile(testCaseList, testCaseFile, failures, failureIds);

        BatchOpsRes batchOpsRes = new BatchOpsRes();
        batchOpsRes.setRetCode(getRetCode(failureIds, testScenarioList, testSuiteList, testCaseList));
        batchOpsRes.setFailures(failures);
        CommonUtil.deleteFile(filePath);
        return batchOpsRes;
    }

    /**
     * get retCode.
     *
     * @param failureIds failure test model ids.
     * @param testScenarioList testScenarioList
     * @param testSuiteList testSuiteList
     * @param testCaseList testCaseList
     * @return retCode
     */
    private int getRetCode(Set<String> failureIds, List<TestScenario> testScenarioList, List<TestSuite> testSuiteList,
        List<TestCase> testCaseList) {
        if (CollectionUtils.isEmpty(failureIds)) {
            return ErrorCode.RET_CODE_SUCCESS;
        } else if (failureIds.size() == testScenarioList.size() + testSuiteList.size() + testCaseList.size()) {
            return ErrorCode.RET_CODE_FAILURE;
        } else {
            return ErrorCode.RET_CODE_PARTIAL_SUCCESS;
        }
    }

    /**
     * update test case script path.
     *
     * @param testCaseList testCaseList
     * @param testCaseFile testCaseFile
     * @param failures fail test model list
     * @param failureIds fail test model ids
     */
    private void updateTestCaseFile(List<TestCase> testCaseList, Map<String, File> testCaseFile,
        List<JSONObject> failures, Set<String> failureIds) {
        testCaseList.forEach(testCase -> {
            if (StringUtils.isNotEmpty(testCase.getNameEn()) && !failureIds.contains(testCase.getId())) {
                // fail test cases do not need to update
                File orgFile = testCaseFile.get(testCase.getNameEn());
                if (null != orgFile) {
                    // do not has test case in test case scenario
                    String testCaseFilePath = Constant.BASIC_TEST_CASE_PATH.concat(testCase.getNameEn())
                        .concat(Constant.UNDER_LINE).concat(testCase.getId());
                    File targetFile = new File(testCaseFilePath);
                    try {
                        FileUtils.copyFile(orgFile, targetFile);
                        testCase.setFilePath(testCaseFilePath);
                        if (Constant.JAVA.equals(testCase.getCodeLanguage())) {
                            testCase.setClassName(CommonUtil.getClassPath(targetFile));
                        }

                        testCaseRepository.update(testCase);
                    } catch (IOException e) {
                        LOGGER.error("copy input stream to file failed. {}", e);
                        failures.add(CommonUtil
                            .setFailureRes(testCase.getId(), testCase.getNameEn(), Constant.TEST_CASE,
                                ErrorCode.FILE_IO_EXCEPTION, ErrorCode.FILE_IO_EXCEPTION_MSG, null));
                        failureIds.add(testCase.getId());
                        // roll back insert
                        testCaseRepository.delete(testCase.getId());
                    } catch (IllegalRequestException e) {
                        LOGGER.error("update repository failed. ");
                        failures.add(CommonUtil
                            .setFailureRes(testCase.getId(), testCase.getNameEn(), Constant.TEST_CASE,
                                ErrorCode.DB_ERROR, String.format(ErrorCode.DB_ERROR_MSG, CREATE_TEST_CASE_FAILED),
                                new ArrayList<String>(Arrays.asList(CREATE_TEST_CASE_FAILED))));
                        failureIds.add(testCase.getId());
                        // roll back insert
                        testCaseRepository.delete(testCase.getId());
                    } finally {
                        CommonUtil.deleteFile(orgFile);
                    }
                } else {
                    // there is not test case script in test case file dir
                    LOGGER.error("there is not test case {} script in test case file dir", testCase.getNameEn());
                    failures.add(CommonUtil.setFailureRes(testCase.getId(), testCase.getNameEn(), Constant.TEST_CASE,
                        ErrorCode.TEST_CASE_NOT_EXISTS_IN_DIR, ErrorCode.TEST_CASE_NOT_EXISTS_IN_DIR_MSG, null));
                    failureIds.add(testCase.getId());
                    testCaseRepository.delete(testCase.getId());
                }
            }
        });
    }

    /**
     * update test scenario icon file.
     *
     * @param testScenarioList testScenarioList
     * @param scenarioIconFile scenarioIconFile
     * @param failures fail test model list
     * @param failureIds fail test model ids
     */
    private void updateScenarioAndIconFile(List<TestScenario> testScenarioList, Map<String, File> scenarioIconFile,
        List<JSONObject> failures, Set<String> failureIds) {
        testScenarioList.forEach(testScenario -> {
            if (StringUtils.isNotEmpty(testScenario.getNameEn()) && !failureIds.contains(testScenario.getId())) {
                File orgFile = scenarioIconFile.get(testScenario.getNameEn());
                String iconFilePath = Constant.BASIC_ICON_PATH.concat(Constant.FILE_TYPE_SCENARIO)
                    .concat(Constant.UNDER_LINE).concat(testScenario.getId()).concat(Constant.DOT).concat("png");
                try {
                    FileUtils.copyFile(orgFile, new File(iconFilePath));
                    AtpFile atpFile = new AtpFile(testScenario.getId(), Constant.FILE_TYPE_SCENARIO,
                        taskRepository.getCurrentDate(), iconFilePath);
                    fileRepository.insertFile(atpFile);
                } catch (IOException e) {
                    LOGGER.error("copy input stream to file failed. {}", e);
                    failures.add(CommonUtil
                        .setFailureRes(testScenario.getId(), testScenario.getNameEn(), Constant.TEST_SCENARIO,
                            ErrorCode.FILE_IO_EXCEPTION, ErrorCode.FILE_IO_EXCEPTION_MSG, null));
                    failureIds.add(testScenario.getId());
                    // roll back insert
                    testScenarioRepository.deleteTestScenario(testScenario.getId());
                } catch (IllegalRequestException e) {
                    LOGGER.error("update repository failed. ");
                    failures.add(CommonUtil
                        .setFailureRes(testScenario.getId(), testScenario.getId(), Constant.TEST_SCENARIO,
                            ErrorCode.DB_ERROR, String.format(ErrorCode.DB_ERROR_MSG, "update repository failed"),
                            new ArrayList<String>(Arrays.asList("update repository failed"))));
                    failureIds.add(testScenario.getId());
                    // roll back insert
                    testScenarioRepository.deleteTestScenario(testScenario.getId());
                } finally {
                    CommonUtil.deleteFile(orgFile);
                }
            }
        });
    }

    /**
     * save test scenario model to db.
     *
     * @param testScenarioList testScenarioList
     * @param failures fail test model list
     * @param failureIds fail test model ids
     */
    private void saveScenario2DB(List<TestScenario> testScenarioList, List<JSONObject> failures,
        Set<String> failureIds) {
        testScenarioList.forEach(testScenario -> {
            if (!failureIds.contains(testScenario.getId())) {
                try {
                    testScenarioRepository.createTestScenario(testScenario);
                } catch (IllegalRequestException e) {
                    LOGGER.error("create test scenario {} failed.", testScenario.getNameEn());
                    failures.add(CommonUtil
                        .setFailureRes(testScenario.getId(), testScenario.getNameEn(), Constant.TEST_SCENARIO,
                            ErrorCode.DB_ERROR, String.format(ErrorCode.DB_ERROR_MSG, "create test scenario failed"),
                            new ArrayList<String>(Arrays.asList(CREATE_TEST_CASE_FAILED))));
                    failureIds.add(testScenario.getId());
                }
            }
        });
    }

    /**
     * save test suite model to db.
     *
     * @param testSuiteList testSuiteList
     * @param failures fail test model list
     * @param failureIds fail test model ids
     */
    private void saveTestSuite2DB(List<TestSuite> testSuiteList, List<JSONObject> failures, Set<String> failureIds) {
        testSuiteList.forEach(testSuite -> {
            if (!failureIds.contains(testSuite.getId())) {
                try {
                    testSuiteRepository.createTestSuite(testSuite);
                } catch (IllegalRequestException e) {
                    LOGGER.error("create test suite {} failed.", testSuite.getNameEn());
                    failures.add(CommonUtil.setFailureRes(testSuite.getId(), testSuite.getNameEn(), Constant.TEST_SUITE,
                        ErrorCode.DB_ERROR, String.format(ErrorCode.DB_ERROR_MSG, "create test suite failed"),
                        new ArrayList<String>(Arrays.asList("create test suite failed"))));
                    failureIds.add(testSuite.getId());
                }
            }
        });
    }

    /**
     * save test case model to db.
     *
     * @param testCaseList testCaseList
     * @param failures fail test model list
     * @param failureIds fail test model ids
     */
    private void saveTestCase2DB(List<TestCase> testCaseList, List<JSONObject> failures, Set<String> failureIds) {
        testCaseList.forEach(testCase -> {
            if (!failureIds.contains(testCase.getId())) {
                try {
                    testCaseRepository.insert(testCase);
                } catch (IllegalRequestException e) {
                    LOGGER.error("create test case {} failed.", testCase.getNameEn());
                    failures.add(CommonUtil
                        .setFailureRes(testCase.getId(), testCase.getNameEn(), Constant.TEST_CASE, ErrorCode.DB_ERROR,
                            String.format(ErrorCode.DB_ERROR_MSG, CREATE_TEST_CASE_FAILED),
                            new ArrayList<String>(Arrays.asList(CREATE_TEST_CASE_FAILED))));
                    failureIds.add(testCase.getId());
                }
            }
        });
    }

    /**
     * check name exists.
     *
     * @param testScenario test scenario model
     */
    public void checkNameExists(TestScenario testScenario) {
        if (null != testScenarioRepository.getTestScenarioByName(testScenario.getNameCh(), null)
            || null != testScenarioRepository.getTestScenarioByName(null, testScenario.getNameEn())) {
            String msg = "name of test scenario already exist.";
            LOGGER.error(msg);
            throw new IllegalArgumentException(msg);
        }
    }

}
