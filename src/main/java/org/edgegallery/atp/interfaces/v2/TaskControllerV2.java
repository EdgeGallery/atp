/*
 * Copyright 2021 Huawei Technologies Co., Ltd.
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

package org.edgegallery.atp.interfaces.v2;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.validation.constraints.Pattern;
import javax.ws.rs.core.MediaType;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.model.ResponseObject;
import org.edgegallery.atp.model.task.TaskRequest;
import org.edgegallery.atp.service.TaskService;
import org.edgegallery.atp.utils.CommonUtil;
import org.edgegallery.atp.utils.exception.FileNotExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RestSchema(schemaId = "testTaskV2")
@RequestMapping("/edgegallery/atp/v2")
@Api(tags = {"APT Test Controller V2"})
@Validated
public class TaskControllerV2 {

    @Autowired
    private TaskService taskService;

    /**
     * create test task.
     * 
     * @param file csar package
     * @return test task info
     */
    @PostMapping(value = "/tasks", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "create test task v2 interface.", response = String.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = "microservice not found", response = String.class),
            @ApiResponse(code = 500, message = "resource grant error", response = String.class)})
    @PreAuthorize("hasRole('ATP_TENANT') || hasRole('ATP_ADMIN')")
    public ResponseEntity<ResponseObject<TaskRequest>> createTest(
            @ApiParam(value = "application files", required = true) @RequestPart("file") MultipartFile file) {
        CommonUtil.validateContext();
        return taskService.createTaskV2(file);
    }

    /**
     * get task by taskId and userId. <br/>
     * this api can be accessed by everyone.
     *
     * @param taskId taskid
     * @return task info
     */
    @GetMapping(value = "/tasks/{taskId}", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "get tasks by taskId.", response = TaskRequest.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = "microservice not found", response = String.class),
            @ApiResponse(code = 500, message = "resource grant error", response = String.class)})
    public ResponseEntity<ResponseObject<TaskRequest>> getTaskById(
            @ApiParam(value = "task id") @PathVariable("taskId") @Pattern(regexp = Constant.REG_ID) String taskId)
            throws FileNotExistsException {
        return taskService.getTaskByIdV2(taskId);
    }

}
