package org.edgegallery.atp.domain.model.test;

import org.edgegallery.atp.domain.shared.PageCriteria;
import org.edgegallery.atp.domain.shared.Page;

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
