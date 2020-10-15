package org.edgegallery.atp.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.model.task.BatchTask;
import org.edgegallery.atp.model.task.TaskRequest;
import org.edgegallery.atp.model.testcase.TestCase;
import org.edgegallery.atp.model.testcase.TestCaseDetail;
import org.edgegallery.atp.model.testcase.TestCaseResult;
import org.edgegallery.atp.model.user.User;
import org.edgegallery.atp.repository.task.BatchTaskRepository;
import org.edgegallery.atp.repository.task.TaskRepository;
import org.edgegallery.atp.repository.testcase.TestCaseRepository;
import org.edgegallery.atp.schedule.testcase.TestCaseManagerImpl;
import org.edgegallery.atp.utils.CommonUtil;
import org.edgegallery.atp.utils.JSONUtil;
import org.edgegallery.atp.utils.TestCaseUtil;
import org.edgegallery.atp.utils.file.FileChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.yaml.snakeyaml.Yaml;

@Service("TaskService")
public class TaskServiceImpl implements TaskService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskServiceImpl.class);

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    TestCaseRepository testCaseRepository;

    @Autowired
    TestCaseManagerImpl testCaseManager;

    @Autowired
    BatchTaskRepository batchTaskRepository;

    @Override
    public String createTask(User user, MultipartFile packages, String accessToken) {
        TaskRequest task = new TaskRequest();
        task.setId(CommonUtil.generateId());
        task.setUser(user);
        task.setAccessToken(accessToken);

        try {
            File tempFile = FileChecker.check(packages, task.getId());
            if (null == tempFile) {
                throw new IllegalArgumentException("temp file is null");
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
    public ResponseEntity<List<TaskRequest>> getTaskById(String userId, String taskId) {
        List<TaskRequest> taskList = new ArrayList<TaskRequest>();
        String batchSubTask = batchTaskRepository.findBatchTask(taskId, userId);
        if (StringUtils.isEmpty(batchSubTask)) {
            // taskId is not batch task id, get single task id from taskTable
            taskList.add(taskRepository.findByTaskIdAndUserId(taskId, userId));
        } else {
            // taskId is batch task id
            String[] taskIdArray = batchSubTask.split(Constant.COMMA);
            for (String id : taskIdArray) {
                taskList.add(taskRepository.findByTaskIdAndUserId(id, userId));
            }
        }
        return ResponseEntity.ok(taskList);
    }

    @Override
    public String downloadTestReport(String taskId, String userId) {
        Map<String, Object> result = new HashMap<String, Object>();
        Yaml yaml = new Yaml();
        String batchSubTask = batchTaskRepository.findBatchTask(taskId, userId);
        if (StringUtils.isEmpty(batchSubTask)) {
            // taskId is not batch task id, get single task id from taskTable
            getTestCaseDetail(taskId, userId, result);
        } else {
            // taskId is batch task id
            String[] taskIdArray = batchSubTask.split(Constant.COMMA);
            for (String id : taskIdArray) {
                getTestCaseDetail(id, userId, result);
            }
        }

        return yaml.dump(result);
    }

    @Override
    public String createBatchTask(User user, List<MultipartFile> packageList, String accessToken) {
        Map<String, File> tempFileList = new HashMap<String, File>();
        StringBuffer subTaskId = new StringBuffer();
        packageList.forEach(file -> {
            String taskId = CommonUtil.generateId();
            File tempFile = FileChecker.check(file, taskId);
            if (null == tempFile) {
                throw new IllegalArgumentException(file.getOriginalFilename() + "temp file is null");
            }
            tempFileList.put(taskId, tempFile);
            subTaskId.append(taskId).append(Constant.COMMA);
        });

        String batchTaskId = initAndSaveBatchTask(user, subTaskId);

        tempFileList.forEach((taskId, tempFile) -> {
            try {
                TaskRequest task = new TaskRequest();
                task.setId(taskId);
                task.setUser(user);
                task.setAccessToken(accessToken);
                String filePath = tempFile.getCanonicalPath();
                initTaskRequset(task, filePath);
                taskRepository.insert(task);
                testCaseManager.executeTestCase(task, filePath);
            } catch (IOException e) {
                LOGGER.error("create task {} failed, file name is: {}", taskId, tempFile.getName());
            }
        });

        return batchTaskId;
    }

    /**
     * init batch task info and store to db.
     * 
     * @param user user info
     * @param subTaskId subTaskId field
     * @return batch task id.
     */
    private String initAndSaveBatchTask(User user, StringBuffer subTaskId) {
        BatchTask batchTask = new BatchTask();
        batchTask.setId(CommonUtil.generateId());
        batchTask.setUserId(user.getUserId());
        batchTask.setUserName(user.getUserName());
        batchTask.setSubTaskId(subTaskId.substring(0, subTaskId.length() - 1));
        batchTaskRepository.insert(batchTask);
        return batchTask.getId();
    }

    /**
     * query task and get testCaseDetail, convert it to map patterm
     * 
     * @param task task
     * @return testCaseDetail of map pattern
     */
    private void getTestCaseDetail(String taskId, String userId, Map<String, Object> result) {
        TaskRequest task = taskRepository.findByTaskIdAndUserId(taskId, userId);
        if (null != task) {
            TestCaseDetail testcaseDetail = task.getTestCaseDetail();
            String str = JSONUtil.marshal(testcaseDetail).replaceAll("\\[|\\]", "");
            Map<String, Object> map = JSONUtil.unMarshal(str, Map.class);
            result.put(taskId, map);
        }
    }

    /**
     * init taskRequest Model
     * 
     * @param user user info
     * @return
     */
    private TaskRequest initTaskRequset(TaskRequest task, String filePath) {
        task.setCreateTime(taskRepository.getCurrentDate());
        task.setStatus(Constant.Status.WAITING);

        List<TestCase> testCaseList = testCaseRepository.findAllTestCases();

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
