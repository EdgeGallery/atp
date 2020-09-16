package org.edgegallery.atp.repository.task;

import org.edgegallery.atp.model.page.Page;
import org.edgegallery.atp.model.page.PageCriteria;
import org.edgegallery.atp.model.task.TaskStatus;

import java.util.List;
import java.util.Optional;

public interface TaskRepository {

    Optional<TaskStatus> find(String taskId);

    Page<TaskStatus> queryAll(PageCriteria pageCriteria);

    String generateId();

    void storeTask(TaskStatus tatus);

    List<TaskStatus> queryAllRunningTasks();

    List<TaskStatus> queryAllSubTasksByTaskId(String taskId);
}
