package org.edgegallery.atp.interfaces.test.facade;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import org.edgegallery.atp.domain.model.test.TaskStatus;
import org.edgegallery.atp.infrastructure.persistence.test.TaskStatusPO;
import org.edgegallery.atp.interfaces.test.facade.dto.TaskDto;
import org.edgegallery.atp.application.TaskService;
import org.edgegallery.atp.domain.model.releases.PackageChecker;
import org.edgegallery.atp.domain.model.releases.UnAuthorizedExecption;
import org.edgegallery.atp.domain.model.user.User;
import org.edgegallery.atp.domain.shared.PageCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service("TaskServiceFacade")
public class TaskServiceFacade {

    @Autowired
    private TaskService taskService;

    public TaskServiceFacade(TaskService taskService) {
        this.taskService = taskService;
    }


    public ResponseEntity<List<TaskDto>> getAllTasks(User user) {
        return ResponseEntity.ok( taskService.queryAllRunningTasks(user.getUserId()).stream().map(TaskDto::of)
                .collect(Collectors.toList()));
    }


    public TaskStatus startTest(User user, MultipartFile packages) {
        PackageChecker checker = new PackageChecker();
        File tempFile = checker.check(packages);
        if(null == tempFile) {
            throw new IllegalArgumentException("file is null");
        }
        return taskService.startTask(user, tempFile);
    }

    public ResponseEntity<TaskDto> getStatusByTaskId(String taskid) {
        return ResponseEntity.ok(TaskDto.of(taskService.find(taskid)));
    }

    public ResponseEntity<TaskDto> getTaskById(User user, String taskid) {
        TaskStatus status = taskService.find(taskid);
        TaskDto dto = TaskDto.of(status);
        if(!status.getUser().getUserId().equals(user.getUserId())) {
            throw new UnAuthorizedExecption(taskid);
        }
        return ResponseEntity.ok(dto);
    }
}
