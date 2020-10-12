package org.edgegallery.atp.schedule.testcase;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.model.task.TaskRequest;
import org.edgegallery.atp.model.testcase.TestCase;
import org.edgegallery.atp.model.testcase.TestCaseDetail;
import org.edgegallery.atp.model.testcase.TestCaseResult;
import org.edgegallery.atp.repository.task.TaskRepository;
import org.edgegallery.atp.repository.testcase.TestCaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestCaseManagerImpl implements TestCaseManager {

    ExecutorService virusTreadPool = Executors.newFixedThreadPool(Constant.MAX_TASK_THREAD_NUM);

    private boolean resultStatus = true;

    @Autowired
    TestCaseRepository testCaseRepository;

    @Autowired
    TaskRepository taskRepository;

    @Override
    public void executeTestCase(TaskRequest task, String filePath) {
        virusTreadPool.execute(new TaskProcessor(task, filePath));
    }

    private class TaskProcessor implements Runnable {

        TaskRequest task;

        String filePath;

        public TaskProcessor(TaskRequest task, String filePath) {
            this.task = task;
            this.filePath = filePath;
        }

        @Override
        public void run() {
            changeStatusAndSave(task);
            TestCaseDetail detail = task.getTestCaseDetail();

            Map<String, String> context = new HashMap<String, String>();
            context.put(Constant.ACCESS_TOKEN, task.getAccessToken());
            context.put(Constant.TENANT_ID, task.getUser().getUserId());

            execute(Constant.testCaseType.COMPLIANCE_TEST, detail.getComplianceTest(), context);
            execute(Constant.testCaseType.VIRUS_SCAN_TEST, detail.getVirusScanningTest(), context);
            execute(Constant.testCaseType.SANDBOX_TEST, detail.getSandboxTest(), context);

            task.setEndTime(taskRepository.getCurrentDate());
            task.setStatus(!resultStatus ? Constant.Status.FAILED : Constant.Status.SUCCESS);
            taskRepository.update(task);

            new File(filePath).delete();
        }

        private void changeStatusAndSave(TaskRequest task) {
            task.setStatus(Constant.Status.RUNNING);
            TestCaseDetail detail = task.getTestCaseDetail();
            changeTestCaseRunning(detail.getComplianceTest());
            changeTestCaseRunning(detail.getSandboxTest());
            changeTestCaseRunning(detail.getVirusScanningTest());
            taskRepository.update(task);
        }

        private void changeTestCaseRunning(List<Map<String, TestCaseResult>> testCases) {
            testCases.forEach(testCaseMap -> {
                for (Map.Entry<String, TestCaseResult> entry : testCaseMap.entrySet()) {
                    TestCaseResult result = entry.getValue();
                    if (null != result) {
                        result.setResult(Constant.Status.RUNNING);
                        entry.setValue(result);
                    }
                }
            });
        }


        private void execute(String type, List<Map<String, TestCaseResult>> testCases, Map<String, String> context) {
            testCases.forEach(testCaseMap -> {
                for (Map.Entry<String, TestCaseResult> entry : testCaseMap.entrySet()) {
                    TestCase testCase = testCaseRepository.findByNameAndType(entry.getKey(), type);
                    TestCaseResult result =
                            TestCaseHandler.getInstantce().testCaseHandler(testCase.getClassName(), filePath, context);
                    if (null != result) {
                        entry.setValue(result);
                    }
                    resultStatus = Constant.Status.FAILED.equals(result.getResult()) ? false : resultStatus;
                }
            });
        }
    }
}
