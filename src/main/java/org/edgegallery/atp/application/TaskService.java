package org.edgegallery.atp.application;

import org.edgegallery.atp.domain.model.test.TaskStatus;
import org.edgegallery.atp.domain.model.test.TaskRepository;
import org.edgegallery.atp.domain.model.testcase.TestCase;
import org.edgegallery.atp.domain.model.testcase.TestCaseRepository;
import org.edgegallery.atp.domain.model.user.User;
import org.edgegallery.atp.domain.shared.Page;
import org.edgegallery.atp.domain.shared.PageCriteria;
import org.edgegallery.atp.domain.shared.exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service("TaskService")
public class TaskService {

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    TestCaseRepository testCaseRepository;

    public Page<TaskStatus> queryAll(PageCriteria pageCriteria) {
        return taskRepository.queryAll(pageCriteria);
    }

    public TaskStatus find(String taskId) {
        return taskRepository.find(taskId).orElseThrow(() -> new EntityNotFoundException(TaskStatus.class, taskId));
    }

    public TaskStatus startTest(User user, File tempFile) {
        String taskId = taskRepository.generateId();
        List<TestCase> testCases = testCaseRepository.queryAllTestCases();

        TaskStatus status = new TaskStatus();


        taskRepository.storeTask(testCases);

        return new TaskStatus();
    }
}
