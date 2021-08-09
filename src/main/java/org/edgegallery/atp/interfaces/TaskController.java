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

package org.edgegallery.atp.interfaces;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.constant.ErrorCode;
import org.edgegallery.atp.model.ResponseObject;
import org.edgegallery.atp.model.task.AnalysisResult;
import org.edgegallery.atp.model.task.IdList;
import org.edgegallery.atp.model.task.TaskRequest;
import org.edgegallery.atp.model.task.TestCaseStatusReq;
import org.edgegallery.atp.service.TaskService;
import org.edgegallery.atp.utils.CommonUtil;
import org.edgegallery.atp.utils.exception.FileNotExistsException;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RestSchema(schemaId = "testTask")
@RequestMapping("/edgegallery/atp/v1")
@Api(tags = {"ATP Test Controller"})
@Validated
public class TaskController {

    @Autowired
    private TaskService taskService;

    /**
     * create test task.
     *
     * @param file csar package
     * @return test task info
     */
    @PostMapping(value = "/tasks", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "create test task.", response = String.class)
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "microservice not found", response = String.class),
        @ApiResponse(code = 500, message = "resource grant error", response = String.class)
    })
    @PreAuthorize("hasRole('ATP_TENANT') || hasRole('ATP_ADMIN')")
    public ResponseEntity<TaskRequest> createTest(
        @ApiParam(value = "application files", required = true) @RequestPart("file") MultipartFile file) {
        CommonUtil.validateContext();
        return ResponseEntity.ok(taskService.createTask(file));
    }

    /**
     * run test task.
     *
     * @param taskId taskId
     * @return test task info
     */
    @PostMapping(value = "/tasks/{taskId}/action/run", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "run test task.", response = String.class)
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "microservice not found", response = String.class),
        @ApiResponse(code = 500, message = "resource grant error", response = String.class)
    })
    @PreAuthorize("hasRole('ATP_TENANT') || hasRole('ATP_ADMIN')")
    public ResponseEntity<TaskRequest> runTest(
        @ApiParam(value = "task id") @PathVariable("taskId") @Pattern(regexp = Constant.REG_ID) String taskId,
        @ApiParam(value = "id of test scenarios selected") @RequestParam("scenarioIdList")
        @Size(max = Constant.LENGTH_255) List<String> scenarioIdList) throws FileNotExistsException {
        CommonUtil.validateContext();
        return ResponseEntity.ok(taskService.runTask(taskId, scenarioIdList));
    }

    /**
     * get all tasks.
     *
     * @return task list
     */
    @GetMapping(value = "/tasks", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "get all tasks by userId.", response = TaskRequest.class)
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "microservice not found", response = String.class),
        @ApiResponse(code = 500, message = "resource grant error", response = String.class)
    })
    @PreAuthorize("hasRole('ATP_GUEST') || hasRole('ATP_TENANT') || hasRole('ATP_ADMIN')")
    public ResponseEntity<List<TaskRequest>> getAllTasks(
        @QueryParam("appName") @Length(max = Constant.LENGTH_64) String appName,
        @QueryParam("status") @Length(max = Constant.LENGTH_64) String status,
        @QueryParam("providerId") @Length(max = Constant.LENGTH_64) String providerId,
        @QueryParam("appVersion") @Length(max = Constant.LENGTH_64) String appVersion) {
        return taskService.getAllTasks(null, appName, status, providerId, appVersion);
    }

    /**
     * get task by taskId and userId. <br/>
     * this api can be accessed by everyone.
     *
     * @param taskId taskid
     * @return task info
     */
    @GetMapping(value = "/tasks/{taskId}", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "get tasks by taskId and userId.", response = TaskRequest.class)
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "microservice not found", response = String.class),
        @ApiResponse(code = 500, message = "resource grant error", response = String.class)
    })
    public ResponseEntity<TaskRequest> getTaskById(
        @ApiParam(value = "task id") @PathVariable("taskId") @Pattern(regexp = Constant.REG_ID) String taskId)
        throws FileNotFoundException {
        return ResponseEntity.ok(taskService.getTaskById(taskId));
    }

    /**
     * batch delete test tasks.
     *
     * @param taskIds the test task id which will be deleted
     * @return fail task id list
     */
    @PostMapping(value = "/tasks/batch_delete", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "batch delete test tasks.", response = String.class)
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "microservice not found", response = String.class),
        @ApiResponse(code = 500, message = "resource grant " + "error", response = String.class)
    })
    @PreAuthorize("hasRole('ATP_ADMIN')")
    public ResponseEntity<Map<String, List<String>>> batchDelete(
        @ApiParam(value = "test task id list") @RequestBody IdList taskIds) {
        return ResponseEntity.ok(taskService.batchDelete(taskIds.getIds()));
    }

    /**
     * test task analysis.
     *
     * @return analysis result
     */
    @GetMapping(value = "/tasks/action/analysize", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "test tasks number analysis", response = String.class)
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "microservice not found", response = String.class),
        @ApiResponse(code = 500, message = "resource grant error", response = String.class)
    })
    @PreAuthorize("hasRole('ATP_GUEST') || hasRole('ATP_TENANT') || hasRole('ATP_ADMIN')")
    public ResponseEntity<AnalysisResult> taskAnalysis() {
        return ResponseEntity.ok(taskService.taskAnalysis());
    }

    /**
     * update test case status.
     *
     * @param taskId taskId
     * @param testCaseStatus testCaseStatus info.
     * @return true
     */
    @PutMapping(value = "/tasks/{taskId}/testcase", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "update test case status", response = Boolean.class)
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "microservice not found", response = String.class),
        @ApiResponse(code = 500, message = "resource grant error", response = String.class)
    })
    @PreAuthorize("hasRole('ATP_ADMIN')")
    public ResponseEntity<Boolean> updateTestCaseStatus(
        @ApiParam(value = "task id") @PathVariable("taskId") @Pattern(regexp = Constant.REG_ID) String taskId,
        @ApiParam(value = "modify test case status request body") @RequestBody List<TestCaseStatusReq> testCaseStatus) {
        return taskService.modifyTestCaseStatus(testCaseStatus, taskId);
    }

    @DeleteMapping(value = "/tasks/{taskId}", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "delete test task by id.", response = Boolean.class)
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "microservice not found", response = String.class),
        @ApiResponse(code = 500, message = "resource grant " + "error", response = String.class)
    })
    @PreAuthorize("hasRole('ATP_ADMIN') || hasRole('ATP_TENANT')")
    public ResponseEntity<Boolean> deleteTaskById(
        @ApiParam(value = "task id") @PathVariable("taskId") @Pattern(regexp = Constant.REG_ID) String taskId) {
        CommonUtil.validateContext();
        return taskService.deleteTaskById(taskId);
    }

    /**
     * upload self-test report.
     *
     * @param taskId taskId
     * @param file self-test report file
     * @return self-test report file path
     */
    @PostMapping(value = "/tasks/{taskId}/action/upload-report")
    @ApiOperation(value = "upload self-test report.", response = String.class)
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "microservice not found", response = String.class),
        @ApiResponse(code = 500, message = "resource grant error", response = String.class)
    })
    @PreAuthorize("hasRole('ATP_TENANT') || hasRole('ATP_ADMIN')")
    public ResponseEntity<ResponseObject<Map<String, String>>> uploadReport(
        @ApiParam(value = "task id") @PathVariable("taskId") @Pattern(regexp = Constant.REG_ID) String taskId,
        @ApiParam(value = "application files", required = true) @RequestPart("file") MultipartFile file)
        throws FileNotExistsException {
        Map<String, String> path = new HashMap<>();
        path.put("path", taskService.uploadReport(taskId, file));
        ResponseObject<Map<String, String>> result = new ResponseObject<Map<String, String>>(path,
            ErrorCode.RET_CODE_SUCCESS, null, "upload self-test report successfully.");
        return ResponseEntity.ok(result);
    }
}
