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

package org.edgegallery.atp.schedule.task;

import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.annotation.PostConstruct;
import org.edgegallery.atp.model.task.TaskRequest;
import org.edgegallery.atp.repository.task.TaskRepository;
import org.edgegallery.atp.repository.testcase.TestCaseRepository;
import org.edgegallery.atp.schedule.testcase.TestCaseManagerImpl;
import org.edgegallery.atp.utils.FileChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class TaskSchedule {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskSchedule.class);

    private static final String BASIC_PATH = FileChecker.getDir() + File.separator + "testCase" + File.separator;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    TestCaseManagerImpl testCaseManager;

    @Autowired
    TestCaseRepository testCaseRepository;

    /**
     * handle exception running task when the atp service start.
     */
    @PostConstruct
    public void handleData() {
        // handle running data
        List<TaskRequest> runningTaskList = taskRepository.queryAllRunningTasks();
        LOGGER.info("handleRunningData runningTaskList: {}", runningTaskList);

        File tempFile = new File(
                new StringBuilder().append(FileChecker.getDir()).append(File.separator).append("temp").toString());
        File[] fileList = tempFile.listFiles();

        runningTaskList.forEach(task -> {
            for (File file : fileList) {
                if (file.getName().startsWith(task.getId())) {
                    try {
                        LOGGER.info("execute task: {}", task.getId());
                        testCaseManager.executeTestCase(task, file.getCanonicalPath());
                    } catch (IOException e) {
                        LOGGER.error("{} get canonical path failed.", file.getName());
                    }
                }
            }
        });
    }
}
