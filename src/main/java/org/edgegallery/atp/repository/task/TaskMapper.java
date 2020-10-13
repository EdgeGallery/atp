package org.edgegallery.atp.repository.task;

import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.edgegallery.atp.model.page.PageCriteria;
import org.edgegallery.atp.model.task.TaskPO;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface TaskMapper {

    /**
     * get task info by taskId and userId
     * 
     * @param taskId taskId
     * @return taskInfo
     */
    TaskPO findByTaskIdAndUserId(String taskId, String userId);

    /**
     * get task list by userId
     * 
     * @param userId
     * @return
     */
    List<TaskPO> findTaskByUserId(String userId);

    Number countTotal(PageCriteria pageCriteria);

    List<TaskPO> findAllWithAppPagination(PageCriteria pageCriteria);

    /**
     * create task info
     * 
     * @param taskPO
     */
    void insert(TaskPO taskPO);

    /**
     * query all running tasks
     * 
     * @return running task info list
     */
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
    Date getCurrentDate();

    /**
     * delete task created before one month
     */
    void delHisTask();
}
