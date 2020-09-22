package org.edgegallery.atp.repository.task;

import java.util.Date;
import java.util.List;
import org.edgegallery.atp.model.page.Page;
import org.edgegallery.atp.model.page.PageCriteria;
import org.edgegallery.atp.model.task.TaskRequest;

public interface TaskRepository {

    /**
     * find task by taskId and userId
     * 
     * @param taskId taskId
     * @param userId userId
     * @return
     */
    TaskRequest findByTaskIdAndUserId(String taskId, String userId);

    /**
     * find task list by userId
     * 
     * @param userId userId
     * @return
     */
    List<TaskRequest> findTaskByUserId(String userId);

    Page<TaskRequest> queryAll(PageCriteria pageCriteria);

    /**
     * generate uuid randomly
     * 
     * @return
     */
    String generateId();

    /**
     * create task info
     * 
     * @param task task
     */
    void insert(TaskRequest task);

    List<TaskRequest> queryAllRunningTasks();

    /**
     * update task info
     * 
     * @param task task
     */
    void update(TaskRequest task);

    /**
     * get DB current time
     * 
     * @return
     */
    Date getCurrentDates();

    /**
     * delete task created before one month
     */
    void delHisTask();

}
