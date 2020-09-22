package org.edgegallery.atp.repository.task;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.edgegallery.atp.model.page.Page;
import org.edgegallery.atp.model.page.PageCriteria;
import org.edgegallery.atp.model.task.TaskRequest;

public interface TaskRepository {

    Optional<TaskRequest> find(String taskId);

    Page<TaskRequest> queryAll(PageCriteria pageCriteria);

    String generateId();

    void insert(TaskRequest task);

    List<TaskRequest> queryAllRunningTasks();

    void update(TaskRequest task);

    Date getCurrentDates();

    void delHisTask();
}
