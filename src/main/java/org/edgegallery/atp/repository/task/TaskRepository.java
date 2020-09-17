package org.edgegallery.atp.repository.task;

import org.edgegallery.atp.model.page.Page;
import org.edgegallery.atp.model.page.PageCriteria;
import org.edgegallery.atp.model.task.TaskRequest;

import java.util.List;
import java.util.Optional;

public interface TaskRepository {

    Optional<TaskRequest> find(String taskId);

    Page<TaskRequest> queryAll(PageCriteria pageCriteria);

    String generateId();

    void storeTask(TaskRequest tatus);

    List<TaskRequest> queryAllRunningTasks();

    List<TaskRequest> queryAllSubTasksByTaskId(String taskId);
}
