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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.constant.ErrorCode;
import org.edgegallery.atp.interfaces.filter.AccessTokenFilter;
import org.edgegallery.atp.model.ResponseObject;
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
import org.edgegallery.atp.utils.exception.FileNotExistsException;
import org.edgegallery.atp.utils.exception.IllegalRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    public TaskRequest runTask(String taskId, List<String> scenarioIdList) {
        try {
            Map<String, String> context = AccessTokenFilter.context.get();
            if (CollectionUtils.isEmpty(scenarioIdList)) {
                String msg = "scenarioIdList is empty.";
                LOGGER.error(msg);
                throw new IllegalArgumentException(msg);
            }
            initTestScenarios(scenarioIdList);
            TaskRequest task = taskRepository.findByTaskIdAndUserId(taskId, context.get(Constant.USER_ID));
            if (null == task) {
                LOGGER.error("get task from db is null.taskId: {}, userId: {}, userName: {}", taskId,
                        context.get(Constant.USER_ID), context.get(Constant.USER_NAME));
                throw new IllegalArgumentException("get task from db is null");
            }
            if (Constant.RUNNING.equals(task.getStatus())) {
                String msg = "this task already in running.";
                LOGGER.error(msg);
                throw new IllegalArgumentException(msg);
            }
            task.setTestScenarios(initTestScenarios(scenarioIdList));
            task.setAccessToken(context.get(Constant.ACCESS_TOKEN));
            task.setStatus(Constant.WAITING);

            taskRepository.update(task);
            String filePath = task.getPackagePath();
            testCaseManager.executeTestCase(task, filePath);

            LOGGER.info("run task successfully.");
            return task;
        } catch (Exception e) {
            LOGGER.error("run task failed. {}", e);
            throw new IllegalArgumentException("run task failed");
        }
    }

    @Override
    public ResponseEntity<Boolean> deleteTaskById(String taskId) {
        Map<String, String> context = AccessTokenFilter.context.get();
        String userId = context.get(Constant.USER_ID);

        TaskRequest task = taskRepository.findByTaskIdAndUserId(taskId, userId);
        if (null != task) {
            taskRepository.deleteTaskById(taskId, userId);
            CommonUtil.deleteFile(task.getPackagePath());
        } else {
            LOGGER.warn("task with id: {}, userId: {} not exists in db.", taskId, userId);
        }
        return ResponseEntity.ok(Boolean.TRUE);
    }

    private List<TaskTestScenario> initTestScenarios(List<String> scenarioIdList) {
        List<TaskTestScenario> result = new ArrayList<TaskTestScenario>();
        scenarioIdList.forEach(scenarioId -> {
            TestScenario testScenario = testScenarioRepository.getTestScenarioById(scenarioId);
            if (null == testScenario) {
                LOGGER.error("scenarioId {} not exists", scenarioId);
                throw new IllegalRequestException(
                    String.format(ErrorCode.NOT_FOUND_EXCEPTION_MSG, "scenarioId: ".concat(scenarioId)),
                    ErrorCode.NOT_FOUND_EXCEPTION,
                    new ArrayList<String>(Arrays.asList("scenarioId: ".concat(scenarioId))));
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
    public ResponseEntity<TaskRequest> getTaskById(String taskId) throws FileNotFoundException {
        TaskRequest response = taskRepository.findByTaskIdAndUserId(taskId, null);
        if (null == response) {
            LOGGER.error("taskId does not exists: {}", taskId);
            throw new FileNotFoundException("taskId does not exists");
        }
        LOGGER.info("get task by id successfully.");
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Map<String, List<String>>> batchDelete(List<String> taskIds) {
        try {
            Map<String, List<String>> response = taskRepository.batchDelete(taskIds);
            LOGGER.info("batch delete successfully.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LOGGER.error("batch delete tasks failed. {}", e);
            throw new IllegalArgumentException("batch delete tasks failed.");
        }
    }

    @Override
    public ResponseEntity<AnalysisResult> taskAnalysis() {
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

        List<TaskRequest> response = taskRepository.findTaskByUserId(null, null, null, null, null);
        AnalysisResult analysisResult = new AnalysisResult();
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
            testScenarioList.forEach(testScenario -> {
                if (testScenario.getId().equals(testCaseStatus.getTestScenarioId())) {
                    List<TaskTestSuite> testSuiteList = testScenario.getTestSuites();
                    testSuiteList.forEach(testSuite -> {
                        if (testSuite.getId().equals(testCaseStatus.getTestSuiteId())) {
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
        return ResponseEntity.ok(Boolean.TRUE);
    }


    @Override
    public ResponseEntity<ResponseObject<TaskRequest>> createTaskV2(MultipartFile file) {
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

            ResponseObject<TaskRequest> result = new ResponseObject<TaskRequest>(task, ErrorCode.RET_CODE_SUCCESS, null,
                    "create task successfully.");
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            LOGGER.error("create task {} failed, file name is: {}", taskId, tempFile.getName());
            throw new IllegalRequestException(ErrorCode.FILE_IO_EXCEPTION_MSG, ErrorCode.FILE_IO_EXCEPTION, null);
        }
    }

    @Override
    public ResponseEntity<ResponseObject<TaskRequest>> getTaskByIdV2(String taskId) throws FileNotExistsException {
        TaskRequest response = taskRepository.findByTaskIdAndUserId(taskId, null);
        if (null == response) {
            LOGGER.error("taskId does not exists: {}", taskId);
            throw new FileNotExistsException(String.format(ErrorCode.NOT_FOUND_EXCEPTION_MSG, Constant.TASK_ID),
                    ErrorCode.NOT_FOUND_EXCEPTION, new ArrayList<String>(Arrays.asList(Constant.TASK_ID)));
        }

        ResponseObject<TaskRequest> result = new ResponseObject<TaskRequest>(response, ErrorCode.RET_CODE_SUCCESS, null,
                "get task by id successfully.");
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<ResponseObject<TaskRequest>> runTaskV2(String taskId, List<String> scenarioIdList) {
        try {
            Map<String, String> context = AccessTokenFilter.context.get();
            if (CollectionUtils.isEmpty(scenarioIdList)) {
                String msg = "scenarioIdList is empty.";
                LOGGER.error(msg);
                throw new IllegalRequestException(String.format(ErrorCode.PARAM_IS_NULL_MSG, "scenarioIdList"),
                        ErrorCode.PARAM_IS_NULL, new ArrayList<String>(Arrays.asList("scenarioIdList")));
            }

            initTestScenarios(scenarioIdList);
            TaskRequest task = taskRepository.findByTaskIdAndUserId(taskId, context.get(Constant.USER_ID));
            if (null == task) {
                LOGGER.error("get task from db is null.taskId: {}, userId: {}, userName: {}", taskId,
                        context.get(Constant.USER_ID), context.get(Constant.USER_NAME));
                throw new IllegalRequestException(String.format(ErrorCode.NOT_FOUND_EXCEPTION_MSG, "get task from db"),
                        ErrorCode.NOT_FOUND_EXCEPTION, new ArrayList<String>(Arrays.asList("get task from db")));
            }

            if (Constant.RUNNING.equals(task.getStatus())) {
                LOGGER.error("this task already in running.");
                throw new IllegalRequestException(ErrorCode.TASK_IS_RUNNING_MSG, ErrorCode.TASK_IS_RUNNING, null);
            }
            task.setTestScenarios(initTestScenarios(scenarioIdList));
            task.setAccessToken(context.get(Constant.ACCESS_TOKEN));
            task.setStatus(Constant.WAITING);

            taskRepository.update(task);
            String filePath = task.getPackagePath();
            testCaseManager.executeTestCase(task, filePath);

            ResponseObject<TaskRequest> result =
                    new ResponseObject<TaskRequest>(task, ErrorCode.RET_CODE_SUCCESS, null, "run task successfully.");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            LOGGER.error("run task failed. {}", e);
            throw new IllegalRequestException(ErrorCode.RUN_TASK_FAILED_MSG, ErrorCode.RUN_TASK_FAILED, null);
        }
    }

    /**
     * confirm task total status.
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
                                    status = Constant.FAILED.equals(testCase.getResult()) ? Constant.FAILED : status;
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
