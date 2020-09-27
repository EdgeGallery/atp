package org.edgegallery.atp.schedule.testcase;

import org.edgegallery.atp.model.task.TaskRequest;

/**
 * test case schedule class.
 *
 */
public interface TestCaseManager {
    /**
     * execute test case asynchronously
     * 
     * @param task test task
     * @param filePath scar file path
     */
    public void executeTestCase(TaskRequest task, String filePath);
}
