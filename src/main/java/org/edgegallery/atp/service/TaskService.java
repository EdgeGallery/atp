/*
 * Copyright 2020-2021 Huawei Technologies Co., Ltd.
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

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import org.edgegallery.atp.model.CommonActionRes;
import org.edgegallery.atp.model.ResponseObject;
import org.edgegallery.atp.model.task.AnalysisResult;
import org.edgegallery.atp.model.task.TaskRequest;
import org.edgegallery.atp.model.task.TestCaseStatusReq;
import org.edgegallery.atp.utils.exception.FileNotExistsException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface TaskService {

    /**
     * run a test task.
     * 
     * @param taskId taskId
     * @param scenarioIdList scenarioIdList
     * @return task info
     */
    TaskRequest runTask(String taskId, List<String> scenarioIdList);

    /**
     * get task info by taskId.
     * 
     * @param taskId taskId
     * @return TaskRequest
     * @throws FileNotFoundException FileNotFoundException
     */
    ResponseEntity<TaskRequest> getTaskById(String taskId) throws FileNotFoundException;

    /**
     * get all task info.
     * 
     * @param userId userId
     * @param appName appName
     * @param status status
     * @param providerId providerId
     * @param appVersion appVersion
     * @return TaskRequest list
     */
    ResponseEntity<List<TaskRequest>> getAllTasks(String userId, String appName, String status,
            String providerId, String appVersion);

    /**
     * precheck before run test task.
     * 
     * @param taskId taskId
     * @return dependency application info.
     */
    CommonActionRes preCheck(String taskId);

    /**
     * create test task.
     * 
     * @param packages csar file
     * @return taskInfo
     */
    TaskRequest createTask(MultipartFile packages);


    /**
     * batch delete tasks by task ids.
     * 
     * @param taskIds taskIds
     * @return delete failed ids
     */
    ResponseEntity<Map<String, List<String>>> batchDelete(List<String> taskIds);

    /**
     * task number analysis.
     * 
     * @return analysis result
     */
    ResponseEntity<AnalysisResult> taskAnalysis();

    /**
     * modify test case status.
     * 
     * @param testCaseStatus test case info
     * @param taskId taskid
     * @return true or throw exception
     */
    ResponseEntity<Boolean> modifyTestCaseStatus(List<TestCaseStatusReq> testCaseStatus, String taskId);
    
    /**
     * create task v2 method.
     * 
     * @param file file
     * @return ResponseObject
     */
    ResponseEntity<ResponseObject<TaskRequest>> createTaskV2(MultipartFile file);

    /**
     * get task by id.
     * 
     * @param taskId taskId
     * @return ResponseObject
     * @throws FileNotExistsException FileNotExistsException
     */
    ResponseEntity<ResponseObject<TaskRequest>> getTaskByIdV2(String taskId) throws FileNotExistsException;
}
