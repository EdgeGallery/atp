package org.edgegallery.atp.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.edgegallery.atp.interfaces.dto.TaskDto;
import org.edgegallery.atp.model.page.Page;
import org.edgegallery.atp.model.page.PageCriteria;
import org.edgegallery.atp.model.task.TaskStatus;
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
		return ResponseEntity.ok(queryAllRunningTasks(user.getUserId()).stream().map(TaskDto::of)
                .collect(Collectors.toList()));
    }

    public TaskStatus startTest(User user, MultipartFile packages) {
		File tempFile = FileChecker.check(packages);
        if(null == tempFile) {
            throw new IllegalArgumentException("file is null");
        }

		String taskId = taskRepository.generateId();
		TaskStatus status = new TaskStatus();
		status.setId(taskId);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		status.setStartTime(simpleDateFormat.format(new Date()));
		// TODO START TASK
		taskRepository.storeTask(status);
		// TODO
		return new TaskStatus();
    }

    public ResponseEntity<TaskDto> getStatusByTaskId(String taskid) {
		return ResponseEntity.ok(TaskDto.of(findTask(taskid)));
    }

    public ResponseEntity<TaskDto> getTaskById(User user, String taskid) {
		TaskStatus status = findTask(taskid);
        TaskDto dto = TaskDto.of(status);
        if(!status.getUser().getUserId().equals(user.getUserId())) {
            throw new UnAuthorizedExecption(taskid);
        }
        return ResponseEntity.ok(dto);
    }

	private Page<TaskStatus> queryAllTask(PageCriteria pageCriteria) {
		return taskRepository.queryAll(pageCriteria);
	}

	private TaskStatus findTask(String taskId) {
		return taskRepository.find(taskId).orElseThrow(() -> new EntityNotFoundException(TaskStatus.class, taskId));
	}

	private List<TaskStatus> queryAllRunningTasks(String userId) {
		return taskRepository.queryAllRunningTasks();
	}
}
