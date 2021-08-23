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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.commons.collections4.CollectionUtils;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.model.config.Config;
import org.edgegallery.atp.model.task.TaskRequest;
import org.edgegallery.atp.model.task.testscenarios.TaskTestCase;
import org.edgegallery.atp.model.task.testscenarios.TaskTestScenario;
import org.edgegallery.atp.model.task.testscenarios.TaskTestSuite;
import org.edgegallery.atp.model.testcase.TestCase;
import org.edgegallery.atp.repository.config.ConfigRepository;
import org.edgegallery.atp.repository.task.TaskRepositoryImpl;
import org.edgegallery.atp.repository.testcase.TestCaseRepository;
import org.edgegallery.atp.utils.JarCallUtil;
import org.edgegallery.atp.utils.JavaCompileUtil;
import org.edgegallery.atp.utils.PythonCallUtil;
import org.edgegallery.atp.utils.SignatureValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TestCaseManagerImpl implements TestCaseManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestCaseManagerImpl.class);

    ExecutorService taskTreadPool = Executors.newFixedThreadPool(Constant.MAX_TASK_THREAD_NUM);

    @Autowired
    TestCaseRepository testCaseRepository;

    @Autowired
    TaskRepositoryImpl taskRepository;

    @Autowired
    ConfigRepository configRepository;

    @Value("${serveraddress.apm:}")
    private String apmServerAddress;

    @Value("${serveraddress.appo:}")
    private String appoServerAddress;

    @Value("${serveraddress.inventory:}")
    private String inventoryServerAddress;

    @Value("${serveraddress.appstore:}")
    private String appstoreServerAddress;

    @Override
    public void executeTestCase(TaskRequest task, String filePath) {
        taskTreadPool.execute(new TaskProcessor(task, filePath));
    }

    /**
     * process test task and schedule test cases.
     *
     */
    private class TaskProcessor implements Runnable {

        TaskRequest task;

        String filePath;

        String resultStatus;

        public TaskProcessor(TaskRequest task, String filePath) {
            this.task = task;
            this.filePath = filePath;
            this.resultStatus = Constant.SUCCESS;
        }

        @Override
        public void run() {
            task.setStatus(Constant.RUNNING);
            taskRepository.update(task);

            Map<String, String> context = new HashMap<String, String>();
            context.put(Constant.ACCESS_TOKEN, task.getAccessToken());
            context.put(Constant.TENANT_ID, task.getUser().getUserId());
            context.put(Constant.APM_SERVER_ADDRESS, apmServerAddress);
            context.put(Constant.APPO_SERVER_ADDRESS, appoServerAddress);
            context.put(Constant.INVENTORY_SERVER_ADDRESS, inventoryServerAddress);
            context.put(Constant.APPSTORE_SERSVER_ADDRESS, appstoreServerAddress);
            //signature
            context.put("signatureResult", SignatureValidation.execute(filePath, context));
            LOGGER.info("signatureResult: {}", context.get("signatureResult"));

            task.getTestScenarios().forEach(testScenario -> {
                parseTestCase(testScenario, context);
            });

            task.setEndTime(taskRepository.getCurrentDate());
            task.setStatus(resultStatus);
            taskRepository.update(task);
        }

        /**
         * parse test case.
         *
         * @param taskTestScenario test scenario info
         * @param context context info
         */
        private void parseTestCase(TaskTestScenario taskTestScenario, Map<String, String> context) {
            List<TaskTestSuite> taskTestSuiteList = taskTestScenario.getTestSuites();
            if (CollectionUtils.isNotEmpty(taskTestSuiteList)) {
                taskTestSuiteList.forEach(taskTestSuite -> {
                    List<TaskTestCase> taskTestCaseList = taskTestSuite.getTestCases();
                    if (CollectionUtils.isNotEmpty(taskTestCaseList)) {
                        executeTestCase(taskTestCaseList, context);
                    }
                });
            }
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

                    switch (testCase.getCodeLanguage()) {
                        case Constant.PYTHON:
                            PythonCallUtil.callPython(testCase, filePath, taskTestCase, context);
                            break;
                        case Constant.JAVA:
                            JavaCompileUtil.executeJava(testCase, filePath, taskTestCase, context);
                            break;
                        case Constant.JAR:
                            JarCallUtil.executeJar(testCase, filePath, taskTestCase, context);
                            break;
                        default:
                            break;
                    }
                    if (!Constant.RUNNING.equals(resultStatus)) {
                        resultStatus = Constant.FAILED.equals(taskTestCase.getResult())
                            ? Constant.FAILED
                            : resultStatus;
                    }
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
    }
}
