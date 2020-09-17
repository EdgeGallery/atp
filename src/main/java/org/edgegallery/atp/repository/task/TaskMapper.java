package org.edgegallery.atp.repository.task;

import org.apache.ibatis.annotations.Mapper;
import org.edgegallery.atp.model.page.PageCriteria;
import org.edgegallery.atp.model.task.TaskPO;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@Mapper
public interface TaskMapper {

    Optional<TaskPO> findByTaskId(String taskId);

    Number countTotal(PageCriteria pageCriteria);

    List<TaskPO> findAllWithAppPagination(PageCriteria pageCriteria);

    void store(TaskPO taskStatusPO);

    List<TaskPO> queryAllRunningTasks();

    List<TaskPO> queryAllSunTasksByTaskId(String taskId);
}
