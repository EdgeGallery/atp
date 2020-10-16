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
import javax.ws.rs.core.MediaType;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.edgegallery.atp.model.task.TaskRequest;
import org.edgegallery.atp.model.user.User;
import org.edgegallery.atp.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
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

    private static final String REG_USER_NAME = "^[a-zA-Z][a-zA-Z0-9_]{5,29}$";

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
    @ApiOperation(value = "start test", response = String.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = "microservice not found", response = String.class),
            @ApiResponse(code = 415, message = "Unprocessable " + "MicroServiceInfo Entity ", response = String.class),
            @ApiResponse(code = 500, message = "resource grant " + "error", response = String.class)})
    @PreAuthorize("hasRole('ATP_TENANT')")
    public ResponseEntity<String> startTest(@RequestParam("userId") @Pattern(regexp = REG_ID) String userId,
            @RequestParam("userName") @Pattern(regexp = REG_USER_NAME) String userName,
            @ApiParam(value = "test yaml files", required = true) @RequestPart("file") MultipartFile packages,
            @RequestParam("accessToken") String accessToken) {
        return ResponseEntity.ok(taskService.createTask(new User(userId, userName), packages, accessToken));
    }

    /**
     * get all tasks according userId
     * 
     * @param userId userId
     * @return task list
     */
    @GetMapping(value = "/tasks", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "get all tasks.", response = TaskRequest.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = "microservice not found", response = String.class),
            @ApiResponse(code = 415, message = "Unprocessable " + "MicroServiceInfo Entity ", response = String.class),
            @ApiResponse(code = 500, message = "resource grant " + "error", response = String.class)})
    @PreAuthorize("hasRole('ATP_TENANT')")
    public ResponseEntity<List<TaskRequest>> getAllTasks(
            @RequestParam("userId") @Pattern(regexp = REG_ID) String userId) {
        return taskService.getAllTasks(userId);
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
    @ApiOperation(value = "get all tasks.", response = TaskRequest.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = "microservice not found", response = String.class),
            @ApiResponse(code = 415, message = "Unprocessable " + "MicroServiceInfo Entity ", response = String.class),
            @ApiResponse(code = 500, message = "resource grant " + "error", response = String.class)})
    @PreAuthorize("hasRole('ATP_TENANT')")
    public ResponseEntity<List<TaskRequest>> getTaskById(
            @RequestParam("userId") @Pattern(regexp = REG_ID) String userId,
            @ApiParam(value = "task id") @PathVariable("taskId") @Pattern(regexp = REG_ID) String taskId) {
        return taskService.getTaskById(userId, taskId);
    }

    @PostMapping(value = "/tasks/{taskId}", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "download test report", response = String.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = "microservice not found", response = String.class),
            @ApiResponse(code = 415, message = "Unprocessable " + "MicroServiceInfo Entity ", response = String.class),
            @ApiResponse(code = 500, message = "resource grant " + "error", response = String.class)})
    @PreAuthorize("hasRole('ATP_TENANT')")
    public ResponseEntity<String> downloadTestReport(@RequestParam("userId") @Pattern(regexp = REG_ID) String userId,
            @ApiParam(value = "task id") @PathVariable("taskId") @Pattern(regexp = REG_ID) String taskId) {
        return ResponseEntity.ok(taskService.downloadTestReport(taskId, userId));
    }

    @PostMapping(value = "/batch/tasks", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "start batch test", response = String.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = "microservice not found", response = String.class),
            @ApiResponse(code = 415, message = "Unprocessable " + "MicroServiceInfo Entity ", response = String.class),
            @ApiResponse(code = 500, message = "resource grant " + "error", response = String.class)})
    @PreAuthorize("hasRole('ATP_TENANT')")
    public ResponseEntity<String> startBatchTest(@RequestParam("userId") @Pattern(regexp = REG_ID) String userId,
            @RequestParam("userName") @Pattern(regexp = REG_USER_NAME) String userName,
            @ApiParam(value = "test yaml file list",
                    required = true) @RequestPart("file") List<MultipartFile> packageList,
            @RequestParam("accessToken") String accessToken) {
        return ResponseEntity.ok(taskService.createBatchTask(new User(userId, userName), packageList, accessToken));
    }
}
