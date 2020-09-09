/*
 *    Copyright 2020 Huawei Technologies Co., Ltd.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.edgegallery.atp.interfaces.test;

import io.swagger.annotations.*;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.edgegallery.atp.interfaces.test.facade.dto.TaskDto;
import org.edgegallery.atp.domain.model.user.User;
import org.edgegallery.atp.interfaces.test.facade.TaskServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Pattern;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Controller
@RestSchema(schemaId = "test")
@RequestMapping("/mec/atp/v1")
@Api(tags = {"APT Test Controller"})
@Validated
public class TaskController {

    private static final String REG_USER_ID = "[0-9a-f]{8}(-[0-9a-f]{4}){3}-[0-9a-f]{12}";

    private static final String REG_USER_NAME = "^[a-zA-Z][a-zA-Z0-9_]{5,29}$";

    private static final String REG_ID = "[0-9a-f]{32}";

    @Autowired
    private TaskServiceFacade taskServiceFacade;

    /**
     * test task.
     */
    @PostMapping(value = "/task", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "start test", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "microservice not found", response = String.class),
            @ApiResponse(code = 415, message = "Unprocessable " + "MicroServiceInfo Entity ", response = String.class),
            @ApiResponse(code = 500, message = "resource grant " + "error", response = String.class)
    })
    @PreAuthorize("hasRole('APPSTORE_TENANT')")
    public ResponseEntity<TaskDto> startTest(@RequestParam("userId") @Pattern(regexp = REG_USER_ID) String userId,
                                             @RequestParam("userName") @Pattern(regexp = REG_USER_NAME) String userName,
                                             @ApiParam(value = "test yaml files", required = true) @RequestPart("file") MultipartFile packages) {
        return ResponseEntity.ok(TaskDto.of(taskServiceFacade.startTest(new User(userId, userName), packages)));
    }

    @GetMapping(value = "/task", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "get all tasks.", response = TaskDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "microservice not found", response = String.class),
            @ApiResponse(code = 415, message = "Unprocessable " + "MicroServiceInfo Entity ", response = String.class),
            @ApiResponse(code = 500, message = "resource grant " + "error", response = String.class)
    })
    @PreAuthorize("hasRole('APPSTORE_TENANT')")
    public ResponseEntity<List<TaskDto>> getAllTasks(
            @RequestParam("userId") @Pattern(regexp = REG_USER_ID) String userId,
            @RequestParam("userName") @Pattern(regexp = REG_USER_NAME) String userName,
            @ApiParam(value = "task id") @PathVariable("taskid")
            @Pattern(regexp = REG_ID) String taskid) {
        return taskServiceFacade.getAllTasks(new User(userId, userName));
    }

    @GetMapping(value = "/task/{taskid}", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "get all tasks.", response = TaskDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "microservice not found", response = String.class),
            @ApiResponse(code = 415, message = "Unprocessable " + "MicroServiceInfo Entity ", response = String.class),
            @ApiResponse(code = 500, message = "resource grant " + "error", response = String.class)
    })
    @PreAuthorize("hasRole('APPSTORE_TENANT')")
    public ResponseEntity<TaskDto> getTaskById(
            @RequestParam("userId") @Pattern(regexp = REG_USER_ID) String userId,
            @RequestParam("userName") @Pattern(regexp = REG_USER_NAME) String userName,
            @ApiParam(value = "task id") @PathVariable("taskid")
            @Pattern(regexp = REG_ID) String taskid) {
        return taskServiceFacade.getTaskById(new User(userId, userName), taskid);
    }
//
//    @GetMapping(value = "task/{taskid}/status", produces = MediaType.APPLICATION_JSON)
//    @ApiOperation(value = "get test status list by task id.", response = TaskDto.class)
//    @ApiResponses(value = {
//            @ApiResponse(code = 404, message = "microservice not found", response = String.class),
//            @ApiResponse(code = 415, message = "Unprocessable " + "MicroServiceInfo Entity ", response = String.class),
//            @ApiResponse(code = 500, message = "resource grant " + "error", response = String.class)
//    })
//    @PreAuthorize("hasRole('APPSTORE_TENANT')")
//    public ResponseEntity<TaskDto> getStatus(@ApiParam(value = "task id") @PathVariable("taskid")
//                                               @Pattern(regexp = REG_ID) String taskid) {
//        return testServiceFacade.getStatusByTaskId(taskid);
//    }
}
