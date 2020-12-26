/*
 * Copyright 2020 Huawei Technologies Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.edgegallery.atp.service;

import java.util.List;
import org.edgegallery.atp.model.CommonActionRes;
import org.edgegallery.atp.model.task.TaskIdList;
import org.edgegallery.atp.model.task.TaskRequest;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface TaskService {

    /**
     * eun a test task
     * 
     * @param taskId taskId
     * @return task info
     */
    public TaskRequest runTask(String taskId);

    /**
     * get task info by taskId
     * 
     * @param user userInfo
     * @param taskid taskId
     * @return task information
     */
    public ResponseEntity<TaskRequest> getTaskById(String taskId);

    /**
     * get all task info
     * 
     * @param user userInfo
     * @return taskInformation list
     */
    public ResponseEntity<List<TaskRequest>> getAllTasks(String userId, String appName, String status,
            String providerId, String appVersion);

    /**
     * download test report by taskId and userId
     * 
     * @param taskId test taskId
     * @param userId
     * @return
     */
    public ResponseEntity<InputStreamResource> downloadTestReport(String taskId, String userId);

    /**
     * precheck before run test task.
     * 
     * @param taskId taskId
     * @return dependency application info.
     */
    public CommonActionRes preCheck(String taskId);

    /**
     * create test task
     * 
     * @param packages csar file
     * @return taskInfo
     */
    public TaskRequest createTask(MultipartFile packages, Boolean isRun);
    
    /**
     * batch get task by taskId and userId
     * 
     * @param userId
     * @param taskList
     * @return
     */
    public ResponseEntity<List<TaskRequest>> batchGetAllTasks(String userId, TaskIdList taskList);
}
