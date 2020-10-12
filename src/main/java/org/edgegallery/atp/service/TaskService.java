package org.edgegallery.atp.service;

import java.util.List;
import org.edgegallery.atp.model.task.TaskRequest;
import org.edgegallery.atp.model.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface TaskService {

    /**
     * create a test task
     * 
     * @param user userInfo
     * @param packages scar file
     * @return taskId
     */
    public String createTask(User user, MultipartFile packages, String accessToken);

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
}
