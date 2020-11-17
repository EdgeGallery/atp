package org.edgegallery.atp.service;

import java.util.List;
import org.edgegallery.atp.model.CommonActionRes;
import org.edgegallery.atp.model.task.TaskRequest;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface TaskService {

    /**
     * create a test task
     * 
     * @param packages scar file
     * @return taskId
     */
    public List<TaskRequest> createTask(MultipartFile[] packages);

    /**
     * get task info by taskId
     * 
     * @param user userInfo
     * @param taskid taskId
     * @return task information
     */
    public ResponseEntity<TaskRequest> getTaskById(String userId, String taskId);

    /**
     * get all task info
     * 
     * @param user userInfo
     * @return taskInformation list
     */
    public ResponseEntity<List<TaskRequest>> getAllTasks(String userId, String appName, String status);

    /**
     * download test report by taskId and userId
     * 
     * @param taskId test taskId
     * @param userId
     * @return
     */
    public ResponseEntity<InputStreamResource> downloadTestReport(String taskId, String userId);

    /**
     * application dependency check.
     * 
     * @param package package file
     * @return dependency application info.
     */
    public CommonActionRes dependencyCheck(MultipartFile packages);
}
