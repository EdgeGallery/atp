package org.edgegallery.atp.interfaces.test.facade;

import org.edgegallery.atp.application.TaskService;
import org.edgegallery.atp.domain.model.releases.PackageChecker;
import org.edgegallery.atp.domain.model.releases.UnAuthorizedExecption;
import org.edgegallery.atp.domain.model.test.TaskStatus;
import org.edgegallery.atp.domain.model.user.User;
import org.edgegallery.atp.domain.shared.PageCriteria;
import org.edgegallery.atp.interfaces.test.facade.dto.StatusDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@Service("TaskServiceFacade")
public class TaskServiceFacade {

    @Autowired
    private TaskService taskService;

    public TaskServiceFacade(TaskService taskService) {
        this.taskService = taskService;
    }


    public ResponseEntity<List<StatusDto>> getAllTasks(User user) {
        return ResponseEntity.ok(taskService.queryAll(new PageCriteria(100, 0, ""))
                .map(StatusDto::of)
                .getResults());
    }


    public TaskStatus startTest(User user, MultipartFile packages) {
        PackageChecker checker = new PackageChecker();
        File tempFile = checker.check(packages);
        if(null == tempFile) {
            throw new IllegalArgumentException("file is null");
        }
        return taskService.startTest(user, tempFile);
    }

    public ResponseEntity<StatusDto> getStatusByTaskId(String taskid) {
        return ResponseEntity.ok(StatusDto.of(taskService.find(taskid)));
    }

    public ResponseEntity<StatusDto> getTaskById(User user, String taskid) {
        StatusDto dto = StatusDto.of(taskService.find(taskid));
        if(!dto.getUserId().equals(user.getUserId())) {
            throw new UnAuthorizedExecption(taskid);
        }
        return ResponseEntity.ok(dto);
    }
}
