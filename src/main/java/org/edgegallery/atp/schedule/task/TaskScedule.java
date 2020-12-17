package org.edgegallery.atp.schedule.task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import javax.annotation.PostConstruct;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.model.task.TaskRequest;
import org.edgegallery.atp.model.testcase.TestCase;
import org.edgegallery.atp.repository.task.TaskRepository;
import org.edgegallery.atp.repository.testcase.TestCaseRepository;
import org.edgegallery.atp.schedule.testcase.TestCaseManagerImpl;
import org.edgegallery.atp.utils.file.FileChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;

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

        // put inner testCase in storage
        try {
            File fileDir = ResourceUtils.getFile("classpath:testCase");
            if (fileDir.exists()) {
                File[] fileArray = fileDir.listFiles();
                for (File file : fileArray) {
                    String className = file.getName().substring(0, file.getName().indexOf(Constant.DOT));
                    TestCase testCase = testCaseRepository.findByClassName(className);
                    String filePath = BASIC_PATH + testCase.getName() + Constant.UNDER_LINE + testCase.getId();
                    FileChecker.createFile(filePath);
                    File result = new File(filePath);
                    FileCopyUtils.copy(file, result);

                    testCase.setFilePath(filePath);
                    testCaseRepository.update(testCase);
                }
            }
        } catch (FileNotFoundException e) {
            LOGGER.error("resource testCase file can not be found");
        } catch (IOException e) {
            LOGGER.error("copy test case to path failed.");
        }
    }
}
