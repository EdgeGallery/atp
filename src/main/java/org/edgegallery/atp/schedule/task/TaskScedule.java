package org.edgegallery.atp.schedule.task;

import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.annotation.PostConstruct;
import org.edgegallery.atp.model.task.TaskRequest;
import org.edgegallery.atp.repository.task.TaskRepository;
import org.edgegallery.atp.schedule.testcase.TestCaseManagerImpl;
import org.edgegallery.atp.utils.file.FileChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Configurable
@EnableScheduling
@EnableAsync
class TestSchedule {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestSchedule.class);

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    TestCaseManagerImpl testCaseManager;

    /**
     * clean task data before one month every 2 clock.
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanHistoryData() {
        taskRepository.delHisTask();
    }

    /**
     * handle exception running task when the atp service start.
     */
    @PostConstruct
    public void handleRunningData() {
        List<TaskRequest> runningTaskList = taskRepository.queryAllRunningTasks();
        File tempFile = new File(
                new StringBuilder().append(FileChecker.getDir()).append(File.separator).append("temp").toString());
        File[] fileList = tempFile.listFiles();

        runningTaskList.forEach(task -> {
            for (File file : fileList) {
                if (file.getName().startsWith(task.getId())) {
                    try {
                        testCaseManager.executeTestCase(task, file.getCanonicalPath());
                    } catch (IOException e) {
                        LOGGER.error("{} get canonical path failed.", file.getName());
                    }
                }
            }
        });
    }
}
