package org.edgegallery.atp.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.edgegallery.atp.interfaces.dto.TaskDto;
import org.edgegallery.atp.model.page.Page;
import org.edgegallery.atp.model.page.PageCriteria;
import org.edgegallery.atp.model.task.TaskRequest;
import org.edgegallery.atp.model.user.User;
import org.edgegallery.atp.repository.task.TaskRepository;
import org.edgegallery.atp.repository.testcase.TestCaseRepository;
import org.edgegallery.atp.utils.exception.EntityNotFoundException;
import org.edgegallery.atp.utils.exception.UnAuthorizedExecption;
import org.edgegallery.atp.utils.file.FileChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service("TaskService")
public class TaskService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskService.class);

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    TestCaseRepository testCaseRepository;

    public ResponseEntity<List<TaskDto>> getAllTasks(User user) {
        return ResponseEntity
                .ok(queryAllRunningTasks(user.getUserId()).stream().map(TaskDto::of).collect(Collectors.toList()));
    }

    public String startTest(User user, MultipartFile packages) {
        File tempFile = FileChecker.check(packages);
        if (null == tempFile) {
            throw new IllegalArgumentException("file is null");
        }

        String taskId = taskRepository.generateId();
        TaskRequest status = new TaskRequest();
        status.setId(taskId);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        status.setCreateTime(simpleDateFormat.format(new Date()));
        // TODO START TASK
        taskRepository.storeTask(status);
        // TODO
        return "taskID";
    }

    public ResponseEntity<TaskDto> getStatusByTaskId(String taskid) {
        return ResponseEntity.ok(TaskDto.of(findTask(taskid)));
    }

    public ResponseEntity<TaskDto> getTaskById(User user, String taskid) {
        TaskRequest status = findTask(taskid);
        TaskDto dto = TaskDto.of(status);
        if (!status.getUser().getUserId().equals(user.getUserId())) {
            throw new UnAuthorizedExecption(taskid);
        }
        return ResponseEntity.ok(dto);
    }

    private Page<TaskRequest> queryAllTask(PageCriteria pageCriteria) {
        return taskRepository.queryAll(pageCriteria);
    }

    private TaskRequest findTask(String taskId) {
        return taskRepository.find(taskId).orElseThrow(() -> new EntityNotFoundException(TaskRequest.class, taskId));
    }

    private List<TaskRequest> queryAllRunningTasks(String userId) {
        return taskRepository.queryAllRunningTasks();
    }
}
