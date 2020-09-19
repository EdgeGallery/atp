package org.edgegallery.atp.application.testcase;

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
import org.edgegallery.atp.utils.CommonUtil;
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
            TestCaseDetail detail = task.getTestCaseDetail();

            execute(Constant.testCaseType.COMPLIANCE_TEST, detail.getComplianceTest());
            execute(Constant.testCaseType.VIRUS_SCAN_TEST, detail.getVirusScanningTest());
            execute(Constant.testCaseType.SANDBOX_TEST, detail.getSandboxTest());

            task.setEndTime(CommonUtil.getFormatDate());
            task.setStatus(!resultStatus ? Constant.Result.FAILED : Constant.Result.SUCCESS);
            taskRepository.storeTask(task);
        }


        private void execute(String type, List<Map<String, TestCaseResult>> testCases) {
            testCases.forEach(testCaseMap -> {
                for (Map.Entry<String, TestCaseResult> entry : testCaseMap.entrySet()) {
                    TestCase testCase = testCaseRepository
                            .findByNameAndType(entry.getKey(), Constant.testCaseType.COMPLIANCE_TEST).get();
                    TestCaseResult result =
                            TestCaseHandler.getInstantce().testCaseHandler(testCase.getClassName(), filePath);
                    if (null != result) {
                        entry.setValue(result);
                    }
                    resultStatus = Constant.Result.FAILED.equals(result.getResult()) ? false : resultStatus;
                }
            });
        }
    }
}
