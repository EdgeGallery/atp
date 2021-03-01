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

package org.edgegallery.atp.service;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import org.apache.commons.collections4.CollectionUtils;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.interfaces.filter.AccessTokenFilter;
import org.edgegallery.atp.model.CommonActionRes;
import org.edgegallery.atp.model.task.AnalysisResult;
import org.edgegallery.atp.model.task.TaskRequest;
import org.edgegallery.atp.model.task.TestCaseStatusReq;
import org.edgegallery.atp.model.task.testscenarios.TaskTestCase;
import org.edgegallery.atp.model.task.testscenarios.TaskTestScenario;
import org.edgegallery.atp.model.task.testscenarios.TaskTestSuite;
import org.edgegallery.atp.model.testcase.TestCase;
import org.edgegallery.atp.model.testscenario.TestScenario;
import org.edgegallery.atp.model.testsuite.TestSuite;
import org.edgegallery.atp.model.user.User;
import org.edgegallery.atp.repository.task.TaskRepository;
import org.edgegallery.atp.repository.testcase.TestCaseRepository;
import org.edgegallery.atp.repository.testscenario.TestScenarioRepository;
import org.edgegallery.atp.repository.testsuite.TestSuiteRepository;
import org.edgegallery.atp.schedule.testcase.TestCaseManagerImpl;
import org.edgegallery.atp.utils.CommonUtil;
import org.edgegallery.atp.utils.FileChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service("TaskService")
public class TaskServiceImpl implements TaskService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskServiceImpl.class);

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    TestCaseRepository testCaseRepository;

    @Autowired
    TestCaseManagerImpl testCaseManager;

    @Autowired
    TestScenarioRepository testScenarioRepository;

    @Autowired
    TestSuiteRepository testSuiteRepository;

    @Override
    public TaskRequest createTask(MultipartFile file) {
        String taskId = CommonUtil.generateId();
        File tempFile = FileChecker.check(file, taskId);

        TaskRequest task = new TaskRequest();
        task.setId(taskId);

        try {
            String filePath = tempFile.getCanonicalPath();
            // init task
            Map<String, String> context = AccessTokenFilter.context.get();
            task.setCreateTime(taskRepository.getCurrentDate());
            task.setStatus(Constant.ATP_CREATED);
            task.setUser(new User(context.get(Constant.USER_ID), context.get(Constant.USER_NAME)));
            task.setPackagePath(filePath);
            Map<String, String> packageInfo = CommonUtil.getPackageInfo(filePath);
            task.setAppName(packageInfo.get(Constant.APP_NAME));
            task.setAppVersion(packageInfo.get(Constant.APP_VERSION));
            task.setProviderId(packageInfo.get(Constant.PROVIDER_ID));

            taskRepository.insert(task);
            LOGGER.info("create task successfully.");
            return task;
        } catch (IOException e) {
            LOGGER.error("create task {} failed, file name is: {}", taskId, tempFile.getName());
            throw new IllegalArgumentException("create task failed.");
        }
    }

    @Override
    public CommonActionRes preCheck(String taskId) {
        CommonActionRes result = new CommonActionRes();
        TaskRequest task =
                taskRepository.findByTaskIdAndUserId(taskId, AccessTokenFilter.context.get().get(Constant.USER_ID));

        if (null == task) {
            throw new IllegalArgumentException("taskId do not exists.");
        }

        String filePath = task.getPackagePath();

        // key is appId, value is packageId
        Stack<Map<String, String>> dependencyAppList = new Stack<Map<String, String>>();
        Map<String, String> context = new HashMap<String, String>();
        context.put(Constant.ACCESS_TOKEN, AccessTokenFilter.context.get().get(Constant.ACCESS_TOKEN));
        CommonUtil.dependencyCheckSchdule(filePath, dependencyAppList, context);

        Map<String, String> getDependencyInfo = new HashMap<String, String>();
        dependencyAppList.forEach(map -> {
            JsonObject response =
                    CommonUtil.getAppInfoFromAppStore(map.get(Constant.APP_ID), map.get(Constant.PACKAGE_ID));
            if (null != response) {
                JsonElement appName = response.get("name");
                JsonElement appVersion = response.get("version");
                getDependencyInfo.put(appName.getAsString(), appVersion.getAsString());
            }
        });
        result.setDependency(getDependencyInfo);

        LOGGER.info("pre-check successfully.");
        return result;
    }

    @Override
    public TaskRequest runTask(String taskId, List<String> scenarioIdList) {
        Map<String, String> context = AccessTokenFilter.context.get();
        initTestScenarios(scenarioIdList);
        TaskRequest task = taskRepository.findByTaskIdAndUserId(taskId, context.get(Constant.USER_ID));
        if(Constant.RUNNING.equals(task.getStatus())) {
            throw new IllegalArgumentException("this task already in running.");
        }
        task.setTestScenarios(initTestScenarios(scenarioIdList));
        task.setAccessToken(context.get(Constant.ACCESS_TOKEN));
        task.setStatus(Constant.WAITING);

        taskRepository.update(task);
        String filePath = task.getPackagePath();
        testCaseManager.executeTestCase(task, filePath);

        LOGGER.info("run task successfully.");
        return task;
    }

    private List<TaskTestScenario> initTestScenarios(List<String> scenarioIdList) {
        List<TaskTestScenario> result = new ArrayList<TaskTestScenario>();
        scenarioIdList.forEach(scenarioId -> {
            TestScenario testScenario = testScenarioRepository.getTestScenarioById(scenarioId);
            if (null == testScenario) {
                LOGGER.error("scenarioId {} not exists", scenarioId);
                throw new IllegalArgumentException("scenarioId not exists.");
            }

            TaskTestScenario scenario = new TaskTestScenario();
            scenario.setId(scenarioId);
            scenario.setNameCh(testScenario.getNameCh());
            scenario.setNameEn(testScenario.getNameEn());
            scenario.setLabel(testScenario.getLabel());

            List<TestSuite> testSuiteList = testSuiteRepository.getAllTestSuites(null, null, scenarioId);
            List<TaskTestSuite> testSuites = new ArrayList<TaskTestSuite>();
            testSuiteList.forEach(testSuite -> {
                TaskTestSuite tempTestSuite = new TaskTestSuite();
                tempTestSuite.setId(testSuite.getId());
                tempTestSuite.setNameCh(testSuite.getNameCh());
                tempTestSuite.setNameEn(testSuite.getNameEn());

                List<TestCase> testCaseList = testCaseRepository.findAllTestCases(null, null, null, testSuite.getId());
                List<TaskTestCase> testCases = new ArrayList<TaskTestCase>();
                testCaseList.forEach(testCase -> {
                    TaskTestCase tempTestCase = new TaskTestCase();
                    tempTestCase.setId(testCase.getId());
                    tempTestCase.setNameCh(testCase.getNameCh());
                    tempTestCase.setNameEn(testCase.getNameEn());
                    tempTestCase.setDescriptionCh(testCase.getDescriptionCh());
                    tempTestCase.setDescriptionEn(testCase.getDescriptionEn());
                    tempTestCase.setType(testCase.getType());
                    tempTestCase.setResult(Constant.WAITING);
                    tempTestCase.setReason(Constant.EMPTY);
                    testCases.add(tempTestCase);
                });
                tempTestSuite.setTestCases(testCases);

                testSuites.add(tempTestSuite);
            });
            scenario.setTestSuites(testSuites);
            result.add(scenario);
        });
        return result;
    }

    @Override
    public ResponseEntity<List<TaskRequest>> getAllTasks(String userId, String appName, String status,
            String providerId, String appVersion) {
        List<TaskRequest> response = taskRepository.findTaskByUserId(userId, appName, status, providerId, appVersion);
        LOGGER.info("get all task successfully.");
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<TaskRequest> getTaskById(String taskId) {
        TaskRequest response = taskRepository.findByTaskIdAndUserId(taskId, null);
        LOGGER.info("get task by id successfully.");
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Map<String, List<String>>> batchDelete(List<String> taskIds) {
        Map<String, List<String>> response = taskRepository.batchDelete(taskIds);
        LOGGER.info("batch delete successfully.");
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<AnalysisResult> taskAnalysis() {
        List<TaskRequest> response = taskRepository.findTaskByUserId(null, null, null, null, null);
        AnalysisResult analysisResult = new AnalysisResult();

        Date curTime = taskRepository.getCurrentDate();
        Calendar calendar = Calendar.getInstance();
        int date = curTime.getDate();
        calendar.setTime(curTime);
        // first day of month
        calendar.add(Calendar.DATE, -date);
        Date firstDayOfMonth = calendar.getTime();
        int oneMonthDays = firstDayOfMonth.getDate();
        calendar.add(Calendar.DATE, -oneMonthDays);
        int twoMonthDays = calendar.getTime().getDate();
        calendar.add(Calendar.DATE, -twoMonthDays);
        int threeMonthDays = calendar.getTime().getDate();
        calendar.add(Calendar.DATE, -threeMonthDays);
        int fourMonthDays = calendar.getTime().getDate();
        calendar.add(Calendar.DATE, -fourMonthDays);
        int fiveMonthDays = calendar.getTime().getDate();
        // get days of * month
        int last2Days = oneMonthDays + twoMonthDays;
        int last3Days = last2Days + threeMonthDays;
        int last4Days = last3Days + fourMonthDays;
        int last5Days = last4Days + fiveMonthDays;

        response.forEach(task -> {
            if (task.getCreateTime().getYear() == curTime.getYear()
                    && task.getCreateTime().getMonth() == curTime.getMonth()) {
                analysisResult.increaseCurrentMonth();
            } else {
                // just consider day, not hours
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    int day = (int) ((dateFormat.parse(dateFormat.format(firstDayOfMonth)).getTime()
                            - dateFormat.parse(dateFormat.format(task.getCreateTime())).getTime())
                            / (1000 * 3600 * 24));
                    if (day < oneMonthDays) {
                        analysisResult.increaseOneMonthAgo();
                    } else if (oneMonthDays <= day && day < last2Days) {
                        analysisResult.increaseTwoMonthAgo();
                    } else if (last2Days <= day && day < last3Days) {
                        analysisResult.increaseThreeMonthAgo();
                    } else if (last3Days <= day && day < last4Days) {
                        analysisResult.increaseFourMonthAgo();
                    } else if (last4Days <= day && day < last5Days) {
                        analysisResult.increaseFiveMonthAgo();
                    }
                } catch (ParseException e) {
                    LOGGER.error("data format parse failed.");
                }
            }
        });

        analysisResult.setTotal();
        return ResponseEntity.ok(analysisResult);
    }
    
    @Override
    public ResponseEntity<Boolean> modifyTestCaseStatus(List<TestCaseStatusReq> testCaseStatusList, String taskId) {
        TaskRequest task = taskRepository.findByTaskIdAndUserId(taskId, null);
        List<TaskTestScenario> testScenarioList = task.getTestScenarios();
        
        testCaseStatusList.forEach(testCaseStatus -> {
            testScenarioList.forEach(testScenario->{
                if(testScenario.getId().equals(testCaseStatus.getTestScenarioId())) {
                    List<TaskTestSuite> testSuiteList = testScenario.getTestSuites();
                    testSuiteList.forEach(testSuite->{
                        if(testSuite.getId().equals(testCaseStatus.getTestSuiteId())) {
                            List<TaskTestCase> testCaseList = testSuite.getTestCases();
                            testCaseList.forEach(testCase -> {
                                if (testCase.getId().equals(testCaseStatus.getTestCaseId())) {
                                    testCase.setResult(testCaseStatus.getResult());
                                    testCase.setReason(testCaseStatus.getReason());
                                }
                            });
                        }
                    });
                }
            });
        });

        confirmTaskStatus(task);
        taskRepository.update(task);
        return ResponseEntity.ok(true);
    }

    /**
     * confirm task total status
     * 
     * @param task task info
     */
    private void confirmTaskStatus(TaskRequest task) {
        String status = Constant.SUCCESS;
        for (TaskTestScenario testScenario : task.getTestScenarios()) {
            List<TaskTestSuite> testSuiteList = testScenario.getTestSuites();
            if (CollectionUtils.isNotEmpty(testSuiteList)) {
                for (TaskTestSuite testSuite : testSuiteList) {
                    List<TaskTestCase> testCases = testSuite.getTestCases();
                    if (CollectionUtils.isNotEmpty(testCases)) {
                        for (TaskTestCase testCase : testCases) {
                            if (Constant.RUNNING.equals(testCase.getResult())) {
                                status = Constant.RUNNING;
                            } else {
                                if (!Constant.RUNNING.equals(status)) {
                                    status = (Constant.FAILED.equals(testCase.getResult()) ? Constant.FAILED : status);
                                }
                            }
                        }
                    }
                }
            }
        }
        task.setStatus(status);
    }
}
