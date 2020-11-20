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

package org.edgegallery.atp.interfaces;

import java.util.List;
import javax.validation.constraints.Pattern;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.interfaces.filter.AccessTokenFilter;
import org.edgegallery.atp.model.CommonActionRes;
import org.edgegallery.atp.model.task.TaskRequest;
import org.edgegallery.atp.service.TaskService;
import org.edgegallery.atp.utils.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
@RestSchema(schemaId = "testTask")
@RequestMapping("/edgegallery/atp/v1")
@Api(tags = {"APT Test Controller"})
@Validated
public class TaskController {
    private static final String REG_ID = "[0-9a-f]{8}(-[0-9a-f]{4}){3}-[0-9a-f]{12}";

    @Autowired
    private TaskService taskService;

    /**
     * create test task
     * 
     * @param userId userId
     * @param userName userName
     * @param packages csar package
     * @return taskId
     */
    @PostMapping(value = "/tasks", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "start test task.", response = String.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = "microservice not found", response = String.class),
            @ApiResponse(code = 415, message = "Unprocessable " + "MicroServiceInfo Entity ", response = String.class),
            @ApiResponse(code = 500, message = "resource grant " + "error", response = String.class)})
    @PreAuthorize("hasRole('ATP_TENANT')")
    public ResponseEntity<List<TaskRequest>> startTest(
            @ApiParam(value = "application files", required = true) @RequestParam("file") MultipartFile[] packageList) {
        return ResponseEntity.ok(taskService.createTask(packageList));
    }

    /**
     * get all tasks according userId
     * 
     * @param userId userId
     * @return task list
     */
    @GetMapping(value = "/tasks", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "get all tasks by userId.", response = TaskRequest.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = "microservice not found", response = String.class),
            @ApiResponse(code = 415, message = "Unprocessable " + "MicroServiceInfo Entity ", response = String.class),
            @ApiResponse(code = 500, message = "resource grant " + "error", response = String.class)})
    @PreAuthorize("hasRole('ATP_GUEST')")
    public ResponseEntity<List<TaskRequest>> getAllTasks(@QueryParam("appName") String appName,
            @QueryParam("status") String status) {
        CommonUtil.validateContext();
        CommonUtil.lengthCheck(appName);
        CommonUtil.lengthCheck(status);
        return taskService.getAllTasks(AccessTokenFilter.context.get().get(Constant.USER_ID), appName, status);
    }

    /**
     * get task by taskId and userId
     * 
     * @param userId userId
     * @param userName userName
     * @param taskid taskid
     * @return task info
     */
    @GetMapping(value = "/tasks/{taskId}", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "get tasks by taskId and userId.", response = TaskRequest.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = "microservice not found", response = String.class),
            @ApiResponse(code = 415, message = "Unprocessable " + "MicroServiceInfo Entity ", response = String.class),
            @ApiResponse(code = 500, message = "resource grant " + "error", response = String.class)})
    @PreAuthorize("hasRole('ATP_GUEST')")
    public ResponseEntity<TaskRequest> getTaskById(
            @ApiParam(value = "task id") @PathVariable("taskId") @Pattern(regexp = REG_ID) String taskId) {
        CommonUtil.validateContext();
        return taskService.getTaskById(AccessTokenFilter.context.get().get(Constant.USER_ID), taskId);
    }

    @GetMapping(value = "/tasks/{taskId}/action/download", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "download test report", response = InputStreamResource.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = "microservice not found", response = String.class),
            @ApiResponse(code = 415, message = "Unprocessable " + "MicroServiceInfo Entity ", response = String.class),
            @ApiResponse(code = 500, message = "resource grant " + "error", response = String.class)})
    @PreAuthorize("hasRole('ATP_TENANT')")
    public ResponseEntity<InputStreamResource> downloadTestReport(
            @ApiParam(value = "task id") @PathVariable("taskId") @Pattern(regexp = REG_ID) String taskId) {
        CommonUtil.validateContext();
        return taskService.downloadTestReport(taskId, AccessTokenFilter.context.get().get(Constant.USER_ID));
    }

    @PostMapping(value = "/common-action/analysis-app", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "application dependency check.", response = CommonActionRes.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = "microservice not found", response = String.class),
            @ApiResponse(code = 415, message = "Unprocessable " + "MicroServiceInfo Entity ", response = String.class),
            @ApiResponse(code = 500, message = "resource grant " + "error", response = String.class)})
    @PreAuthorize("hasRole('ATP_TENANT')")
    public ResponseEntity<CommonActionRes> dependencyCheck(
            @ApiParam(value = "application files", required = true) @RequestPart("file") MultipartFile packages) {
        CommonUtil.validateContext();
        return ResponseEntity.ok(taskService.dependencyCheck(packages));
    }

}
