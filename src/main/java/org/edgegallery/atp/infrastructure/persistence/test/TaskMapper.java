package org.edgegallery.atp.infrastructure.persistence.test;

import org.apache.ibatis.annotations.Mapper;
import org.edgegallery.atp.domain.shared.PageCriteria;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@Mapper
public interface TaskMapper {

    Optional<TaskStatusPO> findByTaskId(String taskId);

    Number countTotal(PageCriteria pageCriteria);

    List<TaskStatusPO> findAllWithAppPagination(PageCriteria pageCriteria);

    void store(TaskStatusPO taskStatusPO);

    List<TaskStatusPO> queryAllRunningTasks();

    List<TaskStatusPO> queryAllSunTasksByTaskId(String taskId);
}
