package org.edgegallery.atp.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.interfaces.filter.AccessTokenFilter;
import org.edgegallery.atp.model.CommonActionRes;
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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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
    public List<TaskRequest> createTask(List<MultipartFile> packageList) {
        Map<String, File> tempFileList = new HashMap<String, File>();
        List<TaskRequest> resultList = new ArrayList<TaskRequest>();
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

        Map<String, String> context = AccessTokenFilter.context.get();
        if (null == context) {
            // TODO delete temp file.
            throw new IllegalArgumentException("AccessTokenFilter.context is null");
        }
        User user = new User(context.get(Constant.USER_ID), context.get(Constant.USER_NAME));

        tempFileList.forEach((taskId, tempFile) -> {
            try {
                TaskRequest task = new TaskRequest();
                task.setId(taskId);
                task.setUser(user);
                task.setAccessToken(context.get(Constant.ACCESS_TOKEN));
                String filePath = tempFile.getCanonicalPath();
                initTaskRequset(task, filePath);
                taskRepository.insert(task);
                testCaseManager.executeTestCase(task, filePath);
                resultList.add(task);
            } catch (IOException e) {
                LOGGER.error("create task {} failed, file name is: {}", taskId, tempFile.getName());
            }
        });

        return resultList;
    }

    @Override
    public ResponseEntity<List<TaskRequest>> getAllTasks(String userId) {
        return ResponseEntity.ok(taskRepository.findTaskByUserId(userId));
    }

    @Override
    public ResponseEntity<TaskRequest> getTaskById(String userId, String taskId) {
        return ResponseEntity.ok((taskRepository.findByTaskIdAndUserId(taskId, userId)));
    }

    @Override
    public String downloadTestReport(String taskId, String userId) {
        Map<String, Object> result = new HashMap<String, Object>();
        Yaml yaml = new Yaml();
        TaskRequest task = taskRepository.findByTaskIdAndUserId(taskId, userId);

        if (null != task) {
            TestCaseDetail testcaseDetail = task.getTestCaseDetail();
            String str = JSONUtil.marshal(testcaseDetail).replaceAll("\\[|\\]", "");
            Map<String, Object> map = JSONUtil.unMarshal(str, Map.class);
            result.put(taskId, map);
        }
        return yaml.dump(result);
    }

    @Override
    public CommonActionRes dependencyCheck(MultipartFile packages) {
        CommonActionRes result = new CommonActionRes();
        String fileId = CommonUtil.generateId();

        File tempFile = FileChecker.check(packages, fileId);
        if (null == tempFile) {
            CommonUtil.deleteTempFile(fileId, packages);
            throw new IllegalArgumentException("temp file is null");
        }

        try {
            String filePath = tempFile.getCanonicalPath();
            // key is appId, value is packageId
            Stack<Map<String, String>> dependencyAppList = new Stack<Map<String, String>>();
            CommonUtil.dependencyCheckSchdule(filePath, dependencyAppList);
            Map<String, String> getDependencyInfo = new HashMap<String, String>();
            dependencyAppList.forEach(map -> {
                JsonObject response =
                        CommonUtil.getAppInfoFromAppStore(map.get(Constant.APP_ID), map.get(Constant.PACKAGE_ID));
                if (null != response) {
                    JsonElement appName = response.get("name");
                    JsonElement appVersion = response.get("version");
                    getDependencyInfo.put(appName.getAsString(), appVersion.getAsString());
                }
            });
            result.setDependency(getDependencyInfo);
        } catch (IOException e) {
            LOGGER.error("get caninical path failed. {}", e.getMessage());
        } finally {
            CommonUtil.deleteTempFile(fileId, packages);
        }

        return result;
    }


    /**
     * init taskRequest Model
     * 
     * @param user user info
     * @return
     */
    private TaskRequest initTaskRequset(TaskRequest task, String filePath) {
        Map<String, String> context = AccessTokenFilter.context.get();
        if (null == context) {
            throw new IllegalArgumentException("AccessTokenFilter.context is null");
        }
        task.setCreateTime(taskRepository.getCurrentDate());
        task.setStatus(Constant.Status.WAITING);
        task.setUser(new User(context.get(Constant.USER_ID), context.get(Constant.USER_NAME)));
        task.setAccessToken(context.get(Constant.ACCESS_TOKEN));
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
