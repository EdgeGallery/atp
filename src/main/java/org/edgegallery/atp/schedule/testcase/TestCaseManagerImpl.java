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

package org.edgegallery.atp.schedule.testcase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.model.config.Config;
import org.edgegallery.atp.model.task.TaskRequest;
import org.edgegallery.atp.model.task.testscenarios.TaskTestCase;
import org.edgegallery.atp.model.task.testscenarios.TaskTestSuite;
import org.edgegallery.atp.model.testcase.TestCase;
import org.edgegallery.atp.repository.config.ConfigRepository;
import org.edgegallery.atp.repository.task.TaskRepository;
import org.edgegallery.atp.repository.testcase.TestCaseRepository;
import org.edgegallery.atp.schedule.config.UrlConfig;
import org.edgegallery.atp.schedule.testcase.executor.TestCaseExecutor;
import org.edgegallery.atp.schedule.testcase.executor.TestCaseExecutorFactory;
import org.edgegallery.atp.utils.SignatureValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class TestCaseManagerImpl {
    @Autowired
    TestCaseRepository testCaseRepository;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    ConfigRepository configRepository;

    @Autowired
    UrlConfig urlConfig;

    @Value("${server.ssl.enabled}")
    boolean sslEnable;

    @Async("taskExecutor")
    public void executeTestCase(TaskRequest task, String filePath) {
        TaskProcessor taskProcessor = new TaskProcessor(task, filePath);
        taskProcessor.doTask();
    }

    /**
     * process test task and schedule test cases.
     */
    private class TaskProcessor {

        TaskRequest task;

        String filePath;

        String resultStatus;

        public TaskProcessor(TaskRequest task, String filePath) {
            this.task = task;
            this.filePath = filePath;
            this.resultStatus = Constant.SUCCESS;
        }

        public void doTask() {
            task.setStatus(Constant.RUNNING);
            taskRepository.update(task);

            Map<String, String> context = new HashMap<String, String>();
            initContext(context, task);

            task.getTestScenarios().forEach(testScenario -> {
                List<TaskTestSuite> taskTestSuiteList = testScenario.getTestSuites();
                if (CollectionUtils.isNotEmpty(taskTestSuiteList)) {
                    taskTestSuiteList.forEach(taskTestSuite -> {
                        List<TaskTestCase> taskTestCaseList = taskTestSuite.getTestCases();
                        if (CollectionUtils.isNotEmpty(taskTestCaseList)) {
                            executeTestCase(taskTestCaseList, context);
                        }
                    });
                }
            });

            task.setEndTime(taskRepository.getCurrentDate());
            task.setStatus(resultStatus);
            taskRepository.update(task);
        }

        /**
         * init context info.
         *
         * @param context context
         * @param task task
         */
        private void initContext(Map<String, String> context, TaskRequest task) {
            context.put(Constant.ACCESS_TOKEN, task.getAccessToken());
            context.put(Constant.TENANT_ID, task.getUser().getUserId());
            context.put(Constant.APM_SERVER_ADDRESS, urlConfig.getApm());
            context.put(Constant.APPO_SERVER_ADDRESS, urlConfig.getAppo());
            context.put(Constant.INVENTORY_SERVER_ADDRESS, urlConfig.getInventory());
            context.put(Constant.APPSTORE_SERSVER_ADDRESS, urlConfig.getAppstore());
            String protocol = sslEnable ? "https://" : "http://";
            context.put("protocol", protocol);
            //signature verify
            context.put(Constant.SIGNATURE_RESULT, SignatureValidation.verify(filePath));
        }

        /**
         * execute test case.
         *
         * @param taskTestCaseList taskTestCaseList
         * @param context context
         */
        private void executeTestCase(List<TaskTestCase> taskTestCaseList, Map<String, String> context) {
            taskTestCaseList.forEach(taskTestCase -> {
                taskTestCase.setResult(Constant.RUNNING);
                taskRepository.update(task);
                // just execute automatic type test case
                if (Constant.TASK_TYPE_AUTOMATIC.equals(taskTestCase.getType())) {
                    TestCase testCase = testCaseRepository
                        .findByName(taskTestCase.getNameCh(), taskTestCase.getNameEn());
                    setConfigParam(testCase, context);

                    TestCaseExecutor executor = TestCaseExecutorFactory.getInstance()
                        .generateExecutor(testCase.getCodeLanguage());
                    executor.executeTestCase(testCase, filePath, taskTestCase, context);

                    resultStatus = setResultStatus(resultStatus, taskTestCase);
                    taskRepository.update(task);
                } else {
                    // have manual test case, the total status is running
                    resultStatus = Constant.RUNNING;
                }
            });
        }

        /**
         * set config param to context.
         *
         * @param testCase test case info
         * @param context context
         */
        private void setConfigParam(TestCase testCase, Map<String, String> context) {
            StringBuffer configParam = new StringBuffer();
            List<String> configIdList = testCase.getConfigIdList();
            if (CollectionUtils.isNotEmpty(configIdList)) {
                configIdList.forEach(id -> {
                    Config config = configRepository.queryConfigById(id);
                    //each config split by comma
                    configParam.append(config.getConfiguration()).append(Constant.COMMA);
                });
            }
            context.put(Constant.CONFIG_PARAM_LIST, configParam.toString());
        }

        /**
         * set result status.
         *
         * @param resultStatus result status
         * @param taskTestCase task test case execute result info
         * @return total result status
         */
        private String setResultStatus(String resultStatus, TaskTestCase taskTestCase) {
            if (!Constant.RUNNING.equals(resultStatus)) {
                resultStatus = Constant.FAILED.equals(taskTestCase.getResult()) ? Constant.FAILED : resultStatus;
            }
            return resultStatus;
        }
    }
}
