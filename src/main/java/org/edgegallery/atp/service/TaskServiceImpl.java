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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.interfaces.filter.AccessTokenFilter;
import org.edgegallery.atp.model.CommonActionRes;
import org.edgegallery.atp.model.task.AnalysisResult;
import org.edgegallery.atp.model.task.TaskIdList;
import org.edgegallery.atp.model.task.TaskRequest;
import org.edgegallery.atp.model.testcase.TestCase;
import org.edgegallery.atp.model.testcase.TestCaseDetail;
import org.edgegallery.atp.model.testcase.TestCaseResult;
import org.edgegallery.atp.model.user.User;
import org.edgegallery.atp.repository.task.TaskRepository;
import org.edgegallery.atp.repository.testcase.TestCaseRepository;
import org.edgegallery.atp.schedule.testcase.TestCaseManagerImpl;
import org.edgegallery.atp.utils.CommonUtil;
import org.edgegallery.atp.utils.FileChecker;
import org.edgegallery.atp.utils.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.yaml.snakeyaml.Yaml;
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

    @Override
    public TaskRequest createTask(MultipartFile file, Boolean isRun) {
        String taskId = CommonUtil.generateId();
        File tempFile = FileChecker.check(file, taskId);

        Map<String, String> context = AccessTokenFilter.context.get();
        User user = new User(context.get(Constant.USER_ID), context.get(Constant.USER_NAME));

        TaskRequest task = new TaskRequest();
        task.setId(taskId);
        task.setUser(user);

        try {
            String filePath = tempFile.getCanonicalPath();
            initTaskRequset(task, filePath);

            if (isRun) {
                Map<String, String> contextInfo = new HashMap<String, String>();
                contextInfo.put(Constant.ACCESS_TOKEN, context.get(Constant.ACCESS_TOKEN));
                CommonUtil.dependencyCheckSchdule(filePath, new Stack<Map<String, String>>(), context);

                task.setAccessToken(context.get(Constant.ACCESS_TOKEN));
                task.setStatus(Constant.WAITING);
                testCaseManager.executeTestCase(task, task.getPackagePath());
            }

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
    public TaskRequest runTask(String taskId) {
        Map<String, String> context = AccessTokenFilter.context.get();
        TaskRequest task = taskRepository.findByTaskIdAndUserId(taskId, context.get(Constant.USER_ID));
        List<TestCase> testCaseList = testCaseRepository.findAllTestCases(null, null, null);
        task.setTestCaseDetail(initTestCaseDetail(testCaseList));

        task.setAccessToken(context.get(Constant.ACCESS_TOKEN));
        task.setStatus(Constant.WAITING);

        taskRepository.update(task);
        String filePath = task.getPackagePath();
        testCaseManager.executeTestCase(task, filePath);

        LOGGER.info("run task successfully.");
        return task;
    }

    @Override
    public ResponseEntity<List<TaskRequest>> getAllTasks(String userId, String appName, String status,
            String providerId, String appVersion) {
        List<TaskRequest> response = taskRepository.findTaskByUserId(userId, appName, status, providerId, appVersion);
        LOGGER.info("get all task successfully.");
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<TaskRequest>> batchGetAllTasks(String userId, TaskIdList taskList) {
        List<TaskRequest> response = taskRepository.batchFindTaskByUserId(userId, taskList);
        LOGGER.info("batch get all task successfully.");
        return ResponseEntity.ok(response);
    }


    @Override
    public ResponseEntity<TaskRequest> getTaskById(String taskId) {
        TaskRequest response = taskRepository.findByTaskIdAndUserId(taskId, null);
        LOGGER.info("get task by id successfully.");
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<InputStreamResource> downloadTestReport(String taskId) {
        Map<String, Object> result = new HashMap<String, Object>();
        Yaml yaml = new Yaml();
        TaskRequest task = taskRepository.findByTaskIdAndUserId(taskId, null);

        if (null != task) {
            TestCaseDetail testcaseDetail = task.getTestCaseDetail();
            String str = JSONUtil.marshal(testcaseDetail).replaceAll("\\[|\\]", "");
            Map<String, Object> map = JSONUtil.unMarshal(str, Map.class);
            result.put(taskId, map);
        }
        String yamlStr = yaml.dump(result);
        InputStream yamlStream = new ByteArrayInputStream(yamlStr.getBytes(StandardCharsets.UTF_8));
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/octet-stream");
        LOGGER.info("download test report successfully.");
        return new ResponseEntity<>(new InputStreamResource(yamlStream), headers, HttpStatus.OK);
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


    /**
     * init taskRequest Model
     * 
     * @param user user info
     * @return
     */
    private TaskRequest initTaskRequset(TaskRequest task, String filePath) {
        Map<String, String> context = AccessTokenFilter.context.get();
        task.setCreateTime(taskRepository.getCurrentDate());
        task.setStatus(Constant.ATP_CREATED);
        task.setUser(new User(context.get(Constant.USER_ID), context.get(Constant.USER_NAME)));
        task.setPackagePath(filePath);
        List<TestCase> testCaseList = testCaseRepository.findAllTestCases(null, null, null);

        if (null != testCaseList) {
            task.setTestCaseDetail(initTestCaseDetail(testCaseList));
        }

        Map<String, String> packageInfo = CommonUtil.getPackageInfo(filePath);
        task.setAppName(packageInfo.get(Constant.APP_NAME));
        task.setAppVersion(packageInfo.get(Constant.APP_VERSION));
        task.setProviderId(packageInfo.get(Constant.PROVIDER_ID));

        return task;
    }


    /**
     * init testCaseDetail model
     * 
     * @param testCaseList testCaseList from DB
     * @return
     */
    private TestCaseDetail initTestCaseDetail(List<TestCase> testCaseList) {
        TestCaseDetail testCaseDetail = new TestCaseDetail();
        List<Map<String, TestCaseResult>> virusList = new ArrayList<Map<String, TestCaseResult>>();
        List<Map<String, TestCaseResult>> complianceList = new ArrayList<Map<String, TestCaseResult>>();
        List<Map<String, TestCaseResult>> sandboxList = new ArrayList<Map<String, TestCaseResult>>();
        Map<String, TestCaseResult> virusMap = new HashMap<String, TestCaseResult>();
        Map<String, TestCaseResult> complianceMap = new HashMap<String, TestCaseResult>();
        Map<String, TestCaseResult> sandboxMap = new LinkedHashMap<String, TestCaseResult>();

        for (TestCase testCase : testCaseList) {
            switch (testCase.getType()) {
                case Constant.SECURITY_TEST:
                    virusMap.put(testCase.getName(), new TestCaseResult());
                    break;
                case Constant.COMPLIANCE_TEST:
                    complianceMap.put(testCase.getName(), new TestCaseResult());
                    break;
                case Constant.SANDBOX_TEST:
                    sandboxMap.put(testCase.getName(), new TestCaseResult());
                    break;
                default:
                    break;
            }
        }

        virusList.add(virusMap);
        complianceList.add(complianceMap);
        sandboxList.add(sandboxMap);
        testCaseDetail.setComplianceTest(complianceList);
        testCaseDetail.setSandboxTest(sandboxList);
        testCaseDetail.setSecurityTest(virusList);

        return testCaseDetail;
    }

}
