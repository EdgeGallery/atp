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

package org.edgegallery.atp.repository.task;

import com.alibaba.fastjson.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.constant.ErrorCode;
import org.edgegallery.atp.model.task.TaskPO;
import org.edgegallery.atp.model.task.TaskRequest;
import org.edgegallery.atp.model.task.testscenarios.TaskTestCase;
import org.edgegallery.atp.model.task.testscenarios.TaskTestCasePo;
import org.edgegallery.atp.model.task.testscenarios.TaskTestScenario;
import org.edgegallery.atp.model.task.testscenarios.TaskTestScenarioPo;
import org.edgegallery.atp.model.task.testscenarios.TaskTestSuite;
import org.edgegallery.atp.model.task.testscenarios.TaskTestSuitePo;
import org.edgegallery.atp.model.testcase.TestCase;
import org.edgegallery.atp.model.testscenario.TestScenario;
import org.edgegallery.atp.model.testsuite.TestSuite;
import org.edgegallery.atp.model.user.User;
import org.edgegallery.atp.repository.mapper.TaskMapper;
import org.edgegallery.atp.repository.testcase.TestCaseRepository;
import org.edgegallery.atp.repository.testscenario.TestScenarioRepository;
import org.edgegallery.atp.repository.testsuite.TestSuiteRepository;
import org.edgegallery.atp.utils.CommonUtil;
import org.edgegallery.atp.utils.exception.IllegalRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class TaskRepositoryImpl implements TaskRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskRepositoryImpl.class);

    @Autowired
    TaskMapper taskMapper;

    @Autowired
    TestCaseRepository testCaseRepository;

    @Autowired
    TestScenarioRepository testScenarioRepository;

    @Autowired
    TestSuiteRepository testSuiteRepository;

    @Value("${servicecomb.uploads.directory}")
    private String uploadPath;

    @Override
    public void insert(TaskRequest task) {
        try {
            taskMapper.insert(TaskPO.of(task));
        } catch (Exception e) {
            LOGGER.error("insert task failed. {}", e);
            throw new IllegalRequestException(String.format(ErrorCode.DB_ERROR_MSG, "insert task failed"),
                ErrorCode.DB_ERROR, new ArrayList<String>(Arrays.asList("insert task failed")));
        }
    }

    @Override
    public void update(TaskRequest task) {
        try {
            taskMapper.update(TaskPO.of(task));
        } catch (Exception e) {
            LOGGER.error("update failed. {}", e);
            throw new IllegalRequestException(String.format(ErrorCode.DB_ERROR_MSG, "update task failed"),
                ErrorCode.DB_ERROR, new ArrayList<String>(Arrays.asList("update task failed")));
        }
    }

    @Override
    public Date getCurrentDate() {
        try {
            return taskMapper.getCurrentDate();
        } catch (Exception e) {
            LOGGER.error("getCurrentDate failed. {}", e);
            throw new IllegalRequestException(String.format(ErrorCode.DB_ERROR_MSG, "getCurrentDate failed"),
                ErrorCode.DB_ERROR, new ArrayList<String>(Arrays.asList("getCurrentDate failed")));
        }
    }

    @Override
    public void delHisTask() {
        try {
            taskMapper.delHisTask();
        } catch (Exception e) {
            LOGGER.error("delHisTask failed. {}", e);
            throw new IllegalRequestException(String.format(ErrorCode.DB_ERROR_MSG, "delHisTask failed"),
                ErrorCode.DB_ERROR, new ArrayList<String>(Arrays.asList("delHisTask failed")));
        }
    }

    @Override
    public TaskRequest findByTaskIdAndUserId(String taskId, String userId) {
        try {
            return null != taskMapper.findByTaskIdAndUserId(taskId, userId) ? toDomain(
                taskMapper.findByTaskIdAndUserId(taskId, userId)) : null;
        } catch (Exception e) {
            LOGGER.error("findByTaskIdAndUserId failed. {}", e);
            throw new IllegalRequestException(String.format(ErrorCode.DB_ERROR_MSG, "findByTaskIdAndUserId failed"),
                ErrorCode.DB_ERROR, new ArrayList<String>(Arrays.asList("findByTaskIdAndUserId failed")));
        }
    }

    @Override
    public List<TaskRequest> findTaskByUserId(String userId, String appName, String status, String providerId,
        String appVersion) {
        try {
            List<TaskPO> taskPoList = taskMapper.findTaskByUserId(userId, appName, status, providerId, appVersion);
            List<TaskRequest> taskRequest = new ArrayList<TaskRequest>();
            taskPoList.stream().filter(Objects::nonNull).forEach(taskPo -> taskRequest.add(toDomain(taskPo)));
            return taskRequest;
        } catch (Exception e) {
            LOGGER.error("findTaskByUserId failed. {}", e);
            throw new IllegalRequestException(String.format(ErrorCode.DB_ERROR_MSG, "findTaskByUserId failed"),
                ErrorCode.DB_ERROR, new ArrayList<String>(Arrays.asList("findTaskByUserId failed")));
        }
    }

    @Override
    public int countTotal(String userId, String appName, String status, String providerId, String appVersion) {
        try {
            return taskMapper.countTotal(userId, appName, status, providerId, appVersion);
        } catch (Exception e) {
            LOGGER.error("get task total count failed. {}", e);
            throw new IllegalRequestException(String.format(ErrorCode.DB_ERROR_MSG, "get task total count failed"),
                ErrorCode.DB_ERROR, new ArrayList<String>(Arrays.asList("get task total count failed")));
        }
    }

    @Override
    public List<TaskRequest> getAllWithPagination(int limit, int offset, String userId, String appName, String status,
        String providerId, String appVersion) {
        try {
            List<TaskPO> taskPoList = taskMapper
                .getAllWithPagination(limit, offset, userId, appName, status, providerId, appVersion);
            List<TaskRequest> taskRequest = new ArrayList<TaskRequest>();
            taskPoList.stream().filter(Objects::nonNull).forEach(taskPo -> taskRequest.add(toDomain(taskPo)));
            return taskRequest;
        } catch (Exception e) {
            LOGGER.error("get all tasks with pagination failed. {}", e);
            throw new IllegalRequestException(
                String.format(ErrorCode.DB_ERROR_MSG, "get all tasks with pagination failed"), ErrorCode.DB_ERROR,
                new ArrayList<String>(Arrays.asList("get all tasks with pagination failed")));
        }
    }

    @Override
    public Map<String, List<String>> batchDelete(List<String> ids) {
        Map<String, List<String>> result = new HashMap<String, List<String>>();
        List<String> failIds = new ArrayList<String>();
        if (CollectionUtils.isNotEmpty(ids)) {
            ids.forEach(id -> {
                try {
                    TaskPO task = taskMapper.findByTaskIdAndUserId(id, null);
                    taskMapper.deleteTaskById(id, null);
                    if (null != task) {
                        CommonUtil.deleteFile(task.getPackagePath());
                        if (StringUtils.isNotEmpty(task.getReportPath())) {
                            CommonUtil.deleteFile(uploadPath.concat(task.getReportPath()));
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("delete task by id {} failed. {}", id, e);
                    failIds.add(id);
                }
            });
        } else {
            LOGGER.warn("ids is empty.");
        }
        result.put(Constant.FAILED, failIds);
        return result;
    }

    @Override
    public void deleteTaskById(String taskId, String userId) {
        try {
            taskMapper.deleteTaskById(taskId, userId);
        } catch (Exception e) {
            LOGGER.error("deleteTaskById failed. {}", e);
            throw new IllegalRequestException(String.format(ErrorCode.DB_ERROR_MSG, "deleteTaskById failed"),
                ErrorCode.DB_ERROR, new ArrayList<String>(Arrays.asList("deleteTaskById failed")));
        }
    }

    private TaskRequest toDomain(TaskPO taskRequsetPo) {
        List<TaskTestScenarioPo> taskTestScenarioPoList = JSONObject
            .parseArray(taskRequsetPo.getTestCaseDetail(), TaskTestScenarioPo.class);
        List<TaskTestScenario> testScenarios = new ArrayList<TaskTestScenario>();
        String status = Constant.SUCCESS;
        boolean isChanged = false;
        if (CollectionUtils.isNotEmpty(taskTestScenarioPoList)) {
            for (TaskTestScenarioPo taskTestScenarioPo : taskTestScenarioPoList) {
                String scenarioId = taskTestScenarioPo.getId();
                TestScenario testScenario = testScenarioRepository.getTestScenarioById(scenarioId);
                if (null == testScenario) {
                    // if the test scenario has been deleted, just ignore it
                    isChanged = true;
                    LOGGER.warn("scenarioId {} not exists", scenarioId);
                    continue;
                }
                TaskTestScenario scenario = setTaskTestScenario(taskTestScenarioPo, testScenario);
                List<TaskTestSuite> testSuites = new ArrayList<TaskTestSuite>();
                if (CollectionUtils.isNotEmpty(taskTestScenarioPo.getTestSuites())) {
                    for (TaskTestSuitePo testSuitePo : taskTestScenarioPo.getTestSuites()) {
                        TaskTestSuite taskTestSuite = new TaskTestSuite(testSuitePo);
                        TestSuite testSuite = testSuiteRepository.getTestSuiteById(testSuitePo.getId());
                        if (null == testSuite) {
                            isChanged = true;
                            LOGGER.warn("testSuiteId {} not exists", testSuitePo.getId());
                            continue;
                        }
                        taskTestSuite.setNameCh(testSuite.getNameCh());
                        taskTestSuite.setNameEn(testSuite.getNameEn());
                        List<TaskTestCase> testCases = new ArrayList<TaskTestCase>();
                        if (CollectionUtils.isNotEmpty(testSuitePo.getTestCases())) {
                            for (TaskTestCasePo testCasePo : testSuitePo.getTestCases()) {
                                TestCase testCaseDb = testCaseRepository.getTestCaseById(testCasePo.getId());
                                if (null == testCaseDb) {
                                    isChanged = true;
                                    LOGGER.warn("testCaseId {} not exists", testCasePo.getId());
                                    continue;
                                }
                                TaskTestCase taskTestCase = setTaskTestCase(testCasePo, testCaseDb);
                                status = calStatus(status, testCasePo);
                                testCases.add(taskTestCase);
                            }
                            taskTestSuite.setTestCases(testCases);
                            testSuites.add(taskTestSuite);
                        }
                    }
                    scenario.setTestSuites(testSuites);
                    testScenarios.add(scenario);
                }
            }

            if (!status.equals(taskRequsetPo.getStatus())) {
                // status refesh because of deleting test scenario or test suite or test case
                taskRequsetPo.setStatus(status);
                taskMapper.update(taskRequsetPo);
            }
        }
        TaskRequest result = setTaskRequest(taskRequsetPo, testScenarios);
        // some test scenario or test suite or test case is changed, need to update
        if (isChanged) {
            update(result);
        }
        return result;
    }

    private TaskTestScenario setTaskTestScenario(TaskTestScenarioPo taskTestScenarioPo, TestScenario testScenario) {
        TaskTestScenario scenario = new TaskTestScenario(taskTestScenarioPo);
        scenario.setNameCh(testScenario.getNameCh());
        scenario.setNameEn(testScenario.getNameEn());
        scenario.setLabel(testScenario.getLabel());
        return scenario;
    }

    private TaskTestCase setTaskTestCase(TaskTestCasePo testCasePo, TestCase testCaseDb) {
        TaskTestCase taskTestCase = new TaskTestCase(testCasePo);
        taskTestCase.setDescriptionCh(testCaseDb.getDescriptionCh());
        taskTestCase.setDescriptionEn(testCaseDb.getDescriptionEn());
        taskTestCase.setNameCh(testCaseDb.getNameCh());
        taskTestCase.setNameEn(testCaseDb.getNameEn());
        taskTestCase.setType(testCaseDb.getType());
        return taskTestCase;
    }

    private TaskRequest setTaskRequest(TaskPO taskRequsetPo, List<TaskTestScenario> testScenarios) {
        TaskRequest result = TaskRequest.builder().setAppName(taskRequsetPo.getAppName())
            .setAppVersion(taskRequsetPo.getAppVersion()).setPackagePath(taskRequsetPo.getPackagePath())
            .setProviderId(taskRequsetPo.getProviderId()).setId(taskRequsetPo.getId())
            .setStatus(taskRequsetPo.getStatus()).setTestCaseDetail(testScenarios)
            .setUser(new User(taskRequsetPo.getUserId(), taskRequsetPo.getUserName()))
            .setReportPath(taskRequsetPo.getReportPath()).build();
        result.setCreateTime(taskRequsetPo.getCreateTime());
        result.setEndTime(taskRequsetPo.getEndTime());
        return result;
    }

    private static String calStatus(String status, TaskTestCasePo testCasePo) {
        if (Constant.RUNNING.equals(testCasePo.getResult())) {
            status = Constant.RUNNING;
        } else {
            if (!Constant.RUNNING.equals(status)) {
                status = Constant.FAILED.equals(testCasePo.getResult()) ? Constant.FAILED : status;
            }
        }
        return status;
    }
}
