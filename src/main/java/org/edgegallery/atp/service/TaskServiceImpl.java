package org.edgegallery.atp.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.edgegallery.atp.application.testcase.TestCaseManagerImpl;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.model.page.PageCriteria;
import org.edgegallery.atp.model.task.TaskRequest;
import org.edgegallery.atp.model.testcase.TestCase;
import org.edgegallery.atp.model.testcase.TestCaseDetail;
import org.edgegallery.atp.model.testcase.TestCaseResult;
import org.edgegallery.atp.model.user.User;
import org.edgegallery.atp.repository.task.TaskRepository;
import org.edgegallery.atp.repository.testcase.TestCaseRepository;
import org.edgegallery.atp.utils.TestCaseUtil;
import org.edgegallery.atp.utils.file.FileChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    public String createTask(User user, MultipartFile packages) {
        TaskRequest task = new TaskRequest();
        task.setId(taskRepository.generateId());
        task.setUser(user);

        try {
            File tempFile = FileChecker.check(packages, task.getId());
            if (null == tempFile) {
                throw new IllegalArgumentException("file is null");
            }
            String filePath = tempFile.getCanonicalPath();

            initTaskRequset(task, filePath);
            taskRepository.insert(task);
            testCaseManager.executeTestCase(task, filePath);
        } catch (IOException e) {
            LOGGER.error("create task failed.");
            return null;
        }

        return task.getId();
    }

    @Override
    public ResponseEntity<List<TaskRequest>> getAllTasks(String userId) {
        return ResponseEntity.ok(taskRepository.findTaskByUserId(userId));
    }

    @Override
    public ResponseEntity<TaskRequest> getTaskById(String userId, String taskId) {
        return ResponseEntity.ok(taskRepository.findByTaskIdAndUserId(taskId, userId));
    }

    /**
     * init taskRequest Model
     * 
     * @param user user info
     * @return
     */
    private TaskRequest initTaskRequset(TaskRequest task, String filePath) {
        task.setCreateTime(taskRepository.getCurrentDates());
        task.setStatus(Constant.Status.WAITING);

        List<TestCase> testCaseList = testCaseRepository.queryAll(new PageCriteria(100, 0, "")).getResults();

        if (null != testCaseList) {
            task.setTestCaseDetail(initTestCaseDetail(testCaseList));
        }

        Map<String, String> appNameAndVersion = TestCaseUtil.getAppNameAndVersion(filePath);
        task.setAppName(appNameAndVersion.get(Constant.APP_NAME));
        task.setAppVersion(appNameAndVersion.get(Constant.APP_VERSION));

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
        Map<String, TestCaseResult> sandboxMap = new HashMap<String, TestCaseResult>();

        for (TestCase testCase : testCaseList) {
            switch (testCase.getType()) {
                case Constant.testCaseType.VIRUS_SCAN_TEST:
                    virusMap.put(testCase.getName(), new TestCaseResult());
                    break;
                case Constant.testCaseType.COMPLIANCE_TEST:
                    complianceMap.put(testCase.getName(), new TestCaseResult());
                    break;
                case Constant.testCaseType.SANDBOX_TEST:
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
        testCaseDetail.setVirusScanningTest(virusList);

        return testCaseDetail;
    }
}
