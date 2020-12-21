package org.edgegallery.atp.schedule.task;

import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.annotation.PostConstruct;
import org.edgegallery.atp.model.task.TaskRequest;
import org.edgegallery.atp.repository.task.TaskRepository;
import org.edgegallery.atp.repository.testcase.TestCaseRepository;
import org.edgegallery.atp.schedule.testcase.TestCaseManagerImpl;
import org.edgegallery.atp.utils.file.FileChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
class TaskSchedule {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskSchedule.class);

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    TestCaseManagerImpl testCaseManager;

    @Autowired
    TestCaseRepository testCaseRepository;

    /**
     * clean task data before one week every 2 clock.
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanHistoryData() {
        taskRepository.delHisTask();
    }

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
