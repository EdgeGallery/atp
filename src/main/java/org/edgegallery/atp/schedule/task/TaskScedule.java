package org.edgegallery.atp.schedule.task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.apache.commons.io.FileUtils;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.model.task.TaskRequest;
import org.edgegallery.atp.model.testcase.TestCase;
import org.edgegallery.atp.repository.task.TaskRepository;
import org.edgegallery.atp.repository.testcase.TestCaseRepository;
import org.edgegallery.atp.schedule.testcase.TestCaseManagerImpl;
import org.edgegallery.atp.utils.FileChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
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

        // put inner testCase in storage, get file dir can not run in jar and linux,maybe need to find other
        // methods
        Map<String, InputStream> testCaseList = new HashMap<String, InputStream>();
        try (InputStream stream1 = getClass().getClassLoader().getResourceAsStream("testCase/BombDefenseTestCase.java");
                InputStream stream2 =
                        getClass().getClassLoader().getResourceAsStream("testCase/InstantiateAppTestCaseInner.java");
                InputStream stream3 =
                        getClass().getClassLoader().getResourceAsStream("testCase/MFContentTestCaseInner.java");
                InputStream stream4 =
                        getClass().getClassLoader().getResourceAsStream("testCase/SourcePathTestCaseInner.java");
                InputStream stream5 =
                        getClass().getClassLoader().getResourceAsStream("testCase/SuffixTestCaseInner.java");
                InputStream stream6 =
                        getClass().getClassLoader().getResourceAsStream("testCase/TOSCAFileTestCaseInner.java");
                InputStream stream7 =
                        getClass().getClassLoader().getResourceAsStream("testCase/UninstantiateAppTestCaseInner.java");
                InputStream stream8 =
                        getClass().getClassLoader().getResourceAsStream("testCase/VirusScanTestCaseInner.java")) {
            testCaseList.put("BombDefenseTestCase", stream1);
            testCaseList.put("InstantiateAppTestCaseInner", stream2);
            testCaseList.put("MFContentTestCaseInner", stream3);
            testCaseList.put("SourcePathTestCaseInner", stream4);
            testCaseList.put("SuffixTestCaseInner", stream5);
            testCaseList.put("TOSCAFileTestCaseInner", stream6);
            testCaseList.put("UninstantiateAppTestCaseInner", stream7);
            testCaseList.put("VirusScanTestCaseInner", stream8);

            for (Map.Entry<String, InputStream> map : testCaseList.entrySet()) {
                TestCase testCase = testCaseRepository.findByClassName(map.getKey());
                String filePath = BASIC_PATH + testCase.getName() + Constant.UNDER_LINE + testCase.getId();
                FileChecker.createFile(filePath);
                File result = new File(filePath);
                FileUtils.copyInputStreamToFile(map.getValue(), result);

                testCase.setFilePath(filePath);
                testCaseRepository.update(testCase);
            }
        } catch (FileNotFoundException e) {
            LOGGER.error("resource testCase file can not be found");
        } catch (IOException e) {
            LOGGER.error("copy test case to path failed.");
        }
    }
}
