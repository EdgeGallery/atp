package org.edgegallery.atp.service;

import java.util.List;
import org.edgegallery.atp.model.task.TaskRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface TaskService {

    /**
     * create a test task
     * 
     * @param packages scar file
     * @return taskId
     */
    public String createTask(MultipartFile packages);

    /**
     * get task info by taskId
     * 
     * @param user userInfo
     * @param taskid taskId
     * @return task information
     */
    public ResponseEntity<List<TaskRequest>> getTaskById(String userId, String taskId);

    /**
     * get all task info
     * 
     * @param user userInfo
     * @return taskInformation list
     */
    public ResponseEntity<List<TaskRequest>> getAllTasks(String userId);

    /**
     * download test report by taskId and userId
     * 
     * @param taskId test taskId
     * @param userId
     * @return
     */
    public String downloadTestReport(String taskId, String userId);

    /**
     * create batch task
     * 
     * @param packageList package list
     * @return batch task id
     */
    public String createBatchTask(List<MultipartFile> packageList);
}
