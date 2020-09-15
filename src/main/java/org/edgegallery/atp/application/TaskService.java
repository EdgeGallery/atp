package org.edgegallery.atp.application;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.edgegallery.atp.domain.model.test.TaskRepository;
import org.edgegallery.atp.domain.model.test.TaskStatus;
import org.edgegallery.atp.domain.model.user.User;
import org.edgegallery.atp.domain.shared.Page;
import org.edgegallery.atp.domain.shared.PageCriteria;
import org.edgegallery.atp.domain.shared.exceptions.EntityNotFoundException;
import org.edgegallery.atp.repository.TestCaseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("TaskService")
public class TaskService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskService.class);

    private Object lock = new Object();

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    TestCaseRepository testCaseRepository;

    public Page<TaskStatus> queryAll(PageCriteria pageCriteria) {
        return  taskRepository.queryAll(pageCriteria);
    }

    public TaskStatus find(String taskId) {
        return taskRepository.find(taskId).orElseThrow(() -> new EntityNotFoundException(TaskStatus.class, taskId));
    }

    public List<TaskStatus> queryAllRunningTasks(String userId) {
        return  taskRepository.queryAllRunningTasks();
    }

    /**
     * start Task
     * @param user
     * @param tempFile
     * @return
     */
    public TaskStatus startTask(User user, File tempFile) {
        String taskId = taskRepository.generateId();
        TaskStatus status = new TaskStatus();
        status.setId(taskId);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        status.setStartTime(simpleDateFormat.format(new Date()));
		// TODO START TASK
        taskRepository.storeTask(status);
        return new TaskStatus();
    }

}
