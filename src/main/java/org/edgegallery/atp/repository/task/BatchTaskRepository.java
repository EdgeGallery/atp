package org.edgegallery.atp.repository.task;

public interface BatchTaskRepository {

    /**
     * get batch task id by taskId and userId
     * 
     * @param taskId batch taskId
     * @param userId userId
     * @return
     */
    String findBatchTask(String id, String userId);
}
