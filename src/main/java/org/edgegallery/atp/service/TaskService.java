package org.edgegallery.atp.service;

import java.util.List;
import org.edgegallery.atp.interfaces.dto.TaskDto;
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
    public String createTask(User user, MultipartFile packages);
    
    /**
     * get task info by taskId
     * 
     * @param user userInfo
     * @param taskid taskId
     * @return task information
     */
    public ResponseEntity<TaskDto> getTaskById(User user, String taskid);

    /**
     * get all task info
     * 
     * @param user userInfo
     * @return taskInformation list
     */
    public ResponseEntity<List<TaskDto>> getAllTasks(User user);
}
