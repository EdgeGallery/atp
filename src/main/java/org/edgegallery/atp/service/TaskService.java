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

import java.util.List;
import java.util.Map;
import org.edgegallery.atp.model.PageResult;
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
    TaskRequest runTask(String taskId, List<String> scenarioIdList) throws FileNotExistsException;

    /**
     * get task info by taskId.
     *
     * @param taskId taskId
     * @return TaskRequest
     * @throws FileNotExistsException FileNotExistsException
     */
    TaskRequest getTaskById(String taskId) throws FileNotExistsException;

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
    ResponseEntity<List<TaskRequest>> getAllTasks(String userId, String appName, String status, String providerId,
        String appVersion);

    /**
     * get all task info by pagination.
     *
     * @param userId userId
     * @param appName appName
     * @param status status
     * @param providerId providerId
     * @param appVersion appVersion
     * @param limit limit
     * @param offset offset
     * @return task info
     */
    PageResult<TaskRequest> getAllTasksByPagination(String userId, String appName, String status, String providerId,
        String appVersion, int limit, int offset);

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
    Map<String, List<String>> batchDelete(List<String> taskIds);

    /**
     * task number analysis.
     *
     * @return analysis result
     */
    AnalysisResult taskAnalysis();

    /**
     * modify test case status.
     * 
     * @param testCaseStatus test case info
     * @param taskId taskid
     * @return true or throw exception
     */
    ResponseEntity<Boolean> modifyTestCaseStatus(List<TestCaseStatusReq> testCaseStatus, String taskId);
    
    /**
     * delete task by id.
     *
     * @param taskId taskId
     * @return true
     */
    ResponseEntity<Boolean> deleteTaskById(String taskId);

    /**
     * upload self-test report.
     *
     * @param taskId taskId
     * @param file self-test report file
     * @return self-report file path
     */
    String uploadReport(String taskId, MultipartFile file) throws FileNotExistsException;
}
