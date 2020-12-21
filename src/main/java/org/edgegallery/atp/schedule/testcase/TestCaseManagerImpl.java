package org.edgegallery.atp.schedule.testcase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.commons.lang3.StringUtils;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.model.task.TaskRequest;
import org.edgegallery.atp.model.testcase.TestCase;
import org.edgegallery.atp.model.testcase.TestCaseDetail;
import org.edgegallery.atp.model.testcase.TestCaseResult;
import org.edgegallery.atp.repository.task.TaskRepositoryImpl;
import org.edgegallery.atp.repository.testcase.TestCaseRepository;
import org.edgegallery.atp.utils.JavaCompileUtil;
import org.edgegallery.atp.utils.PythonCallUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestCaseManagerImpl implements TestCaseManager {

    ExecutorService virusTreadPool = Executors.newFixedThreadPool(Constant.MAX_TASK_THREAD_NUM);

    @Autowired
    TestCaseRepository testCaseRepository;

    @Autowired
    TaskRepositoryImpl taskRepository;

    @Override
    public void executeTestCase(TaskRequest task, String filePath) {
        virusTreadPool.execute(new TaskProcessor(task, filePath));
    }

    /**
     * process test task and schedule test cases.
     *
     */
    private class TaskProcessor implements Runnable {

        TaskRequest task;

        String filePath;

        boolean resultStatus;

        public TaskProcessor(TaskRequest task, String filePath) {
            this.task = task;
            this.filePath = filePath;
            this.resultStatus = true;
        }

        @Override
        public void run() {
            changeStatusAndSave(task);
            TestCaseDetail detail = task.getTestCaseDetail();

            Map<String, String> context = new HashMap<String, String>();
            context.put(Constant.ACCESS_TOKEN, task.getAccessToken());
            context.put(Constant.TENANT_ID, task.getUser().getUserId());

            execute(Constant.SECURITY_TEST, detail.getSecurityTest(), context);
            execute(Constant.COMPLIANCE_TEST, detail.getComplianceTest(), context);
            execute(Constant.SANDBOX_TEST, detail.getSandboxTest(), context);

            task.setEndTime(taskRepository.getCurrentDate());
            task.setStatus(!resultStatus ? Constant.FAILED : Constant.SUCCESS);
            taskRepository.update(task);
        }

        /**
         * change task status from waiting to running.
         * 
         * @param task task info.
         */
        private void changeStatusAndSave(TaskRequest task) {
            task.setStatus(Constant.RUNNING);
            TestCaseDetail detail = task.getTestCaseDetail();
            changeTestCaseRunning(detail.getComplianceTest());
            changeTestCaseRunning(detail.getSandboxTest());
            changeTestCaseRunning(detail.getSecurityTest());
            taskRepository.update(task);
        }

        /**
         * change test case status to running.
         * 
         * @param testCases test case list info.
         */
        private void changeTestCaseRunning(List<Map<String, TestCaseResult>> testCases) {
            testCases.forEach(testCaseMap -> {
                for (Map.Entry<String, TestCaseResult> entry : testCaseMap.entrySet()) {
                    TestCaseResult result = entry.getValue();
                    if (null != result) {
                        result.setResult(Constant.RUNNING);
                        entry.setValue(result);
                    }
                }
            });
        }

        /**
         * schedule test case according to test case type.
         * 
         * @param type test case type
         * @param testCases test case list info
         * @param context context info
         */
        private void execute(String type, List<Map<String, TestCaseResult>> testCases, Map<String, String> context) {
            testCases.forEach(testCaseMap -> {
                for (Map.Entry<String, TestCaseResult> entry : testCaseMap.entrySet()) {
                    TestCase testCase = testCaseRepository.findByNameAndType(entry.getKey(), type);
                    TestCaseResult result = entry.getValue();
                    if (StringUtils.isEmpty(testCase.getFilePath())) {
                        // fixed test case
                        result = TestCaseHandler.getInstantce().testCaseHandler(testCase.getClassName(), filePath,
                                context);
                    } else {
                        switch (testCase.getCodeLanguage()) {
                            case Constant.PYTHON:
                                PythonCallUtil.callPython(testCase.getFilePath(), filePath, result, context);
                                break;
                            case Constant.JAVA:
                                JavaCompileUtil.executeJava(testCase, filePath, result, context);
                                break;
                            default:
                                break;
                        }
                    }
                    
                    result.setVerificationModel(testCase.getVerificationModel());
                    if (null != result) {
                        entry.setValue(result);
                    }
                    resultStatus = Constant.FAILED.equals(result.getResult()) ? false : resultStatus;
                    taskRepository.update(task);
                }
            });
        }
    }
}
