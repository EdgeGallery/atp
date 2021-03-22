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

import org.edgegallery.atp.model.CommonActionRes;
import org.edgegallery.atp.model.task.AnalysisResult;
import org.edgegallery.atp.model.task.TaskRequest;
import org.edgegallery.atp.model.task.TestCaseStatusReq;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

public interface TaskService {

    /**
     * run a test task
     * 
     * @param taskId taskId
     * @return task info
     */
    TaskRequest runTask(String taskId, List<String> scenarioIdList);

    /**
     * get task info by taskId
     * 
     * @param user userInfo
     * @param taskid taskId
     * @return task information
     */
    ResponseEntity<TaskRequest> getTaskById(String taskId) throws FileNotFoundException;

    /**
     * get all task info
     * 
     * @param user userInfo
     * @return taskInformation list
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
     * create test task
     * 
     * @param packages csar file
     * @return taskInfo
     */
    TaskRequest createTask(MultipartFile packages);


    /**
     * batch delete tasks by task ids
     * 
     * @param taskIds
     * @return delete failed ids
     */
    ResponseEntity<Map<String, List<String>>> batchDelete(List<String> taskIds);

    /**
     * task number analysis
     * 
     * @return analysis result
     */
    ResponseEntity<AnalysisResult> taskAnalysis();

    /**
     * modify test case status
     * 
     * @param testCaseStatus test case info
     * @param taskId taskid
     * @return true or throw exception
     */
    ResponseEntity<Boolean> modifyTestCaseStatus(List<TestCaseStatusReq> testCaseStatus, String taskId);
}
