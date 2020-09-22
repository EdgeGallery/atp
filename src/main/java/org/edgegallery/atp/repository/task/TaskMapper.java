package org.edgegallery.atp.repository.task;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.edgegallery.atp.model.page.PageCriteria;
import org.edgegallery.atp.model.task.TaskPO;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface TaskMapper {

    /**
     * get task info by taskId
     * 
     * @param taskId taskId
     * @return taskInfo
     */
    Optional<TaskPO> findByTaskId(String taskId);

    Number countTotal(PageCriteria pageCriteria);

    List<TaskPO> findAllWithAppPagination(PageCriteria pageCriteria);

    /**
     * create task info
     * 
     * @param taskPO
     */
    void insert(TaskPO taskPO);

    List<TaskPO> queryAllRunningTasks();

    /**
     * update task info.
     * 
     * @param taskPO
     */
    void update(TaskPO taskPO);

    /**
     * get current db time
     * 
     * @return current time
     */
    Date getCurrentDates();

    /**
     * delete task created before one month
     */
    void delHisTask();
}
