package org.edgegallery.atp.repository.task;

import org.edgegallery.atp.model.task.BatchTask;

public interface BatchTaskRepository {

    /**
     * get batch task id by taskId and userId
     * 
     * @param taskId batch taskId
     * @param userId userId
     * @return
     */
    String findBatchTask(String id, String userId);

    /**
     * insert into batch task table.
     * 
     * @param batchTask batch task info.
     */
    void insert(BatchTask batchTask);
}
