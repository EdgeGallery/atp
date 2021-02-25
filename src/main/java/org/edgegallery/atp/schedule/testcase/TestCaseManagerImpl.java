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

package org.edgegallery.atp.schedule.testcase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.commons.collections4.CollectionUtils;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.model.task.TaskRequest;
import org.edgegallery.atp.model.task.testscenarios.TaskTestCase;
import org.edgegallery.atp.model.task.testscenarios.TaskTestScenario;
import org.edgegallery.atp.model.task.testscenarios.TaskTestSuite;
import org.edgegallery.atp.model.testcase.TestCase;
import org.edgegallery.atp.repository.task.TaskRepositoryImpl;
import org.edgegallery.atp.repository.testcase.TestCaseRepository;
import org.edgegallery.atp.utils.JarCallUtil;
import org.edgegallery.atp.utils.JavaCompileUtil;
import org.edgegallery.atp.utils.PythonCallUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestCaseManagerImpl implements TestCaseManager {

    ExecutorService taskTreadPool = Executors.newFixedThreadPool(Constant.MAX_TASK_THREAD_NUM);

    @Autowired
    TestCaseRepository testCaseRepository;

    @Autowired
    TaskRepositoryImpl taskRepository;

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

            task.getTestScenarios().forEach(testScenario -> {
                execute(testScenario, context);
            });

            task.setEndTime(taskRepository.getCurrentDate());
            task.setStatus(resultStatus);
            taskRepository.update(task);
        }

        /**
         * schedule test case
         * 
         * @param taskTestScenario test scenario info
         * @param context context info
         */
        private void execute(TaskTestScenario taskTestScenario, Map<String, String> context) {
            List<TaskTestSuite> taskTestSuiteList = taskTestScenario.getTestSuites();
            if (CollectionUtils.isNotEmpty(taskTestSuiteList)) {
                taskTestSuiteList.forEach(taskTestSuite -> {
                    List<TaskTestCase> taskTestCaseList = taskTestSuite.getTestCases();
                    if (CollectionUtils.isNotEmpty(taskTestCaseList)) {
                        taskTestCaseList.forEach(taskTestCase -> {
                            taskTestCase.setResult(Constant.RUNNING);
                            taskRepository.update(task);
                            // just execute automatic type test case
                            if (Constant.TASK_TYPE_AUTOMATIC.equals(taskTestCase.getType())) {
                                TestCase testCase = testCaseRepository.findByName(taskTestCase.getNameCh(),
                                        taskTestCase.getNameEn());
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
                                    resultStatus = (Constant.FAILED.equals(taskTestCase.getResult()) ? Constant.FAILED
                                            : resultStatus);
                                }
                                taskRepository.update(task);
                            } else {
                                // have manual test case, the total status is running
                                resultStatus = Constant.RUNNING;
                            }
                        });
                    }
                });
            }
        }
    }
}
