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

package org.edgegallery.atp.interfaces.testcase;

import io.swagger.annotations.*;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.edgegallery.atp.domain.model.user.User;
import org.edgegallery.atp.interfaces.testcase.facade.TestCaseServiceFacade;
import org.edgegallery.atp.interfaces.testcase.facade.dto.TestCaseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Pattern;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;

@Controller
@RestSchema(schemaId = "testcase")
@RequestMapping("/mec/apt/v1")
@Api(tags = {"APT Test Case Controller"})
@Validated
public class TestCaseController {

    private static final String REG_USER_ID = "[0-9a-f]{8}(-[0-9a-f]{4}){3}-[0-9a-f]{12}";

    private static final String REG_USER_NAME = "^[a-zA-Z][a-zA-Z0-9_]{5,29}$";

    private static final String REG_ID = "[0-9a-f]{32}";

    @Autowired
    private TestCaseServiceFacade testCaseServiceFacade;

    /**
     * test case upload function.
     */
    @PostMapping(value = "/testcase", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "upload testcase yaml", response = String.class)
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "microservice not found", response = String.class),
        @ApiResponse(code = 415, message = "Unprocessable " + "MicroServiceInfo Entity ", response = String.class),
        @ApiResponse(code = 500, message = "resource grant " + "error", response = String.class)
    })
    @PreAuthorize("hasRole('APPSTORE_TENANT')")
    public ResponseEntity<String> testCaseUpload(@RequestParam("userId") @Pattern(regexp = REG_USER_ID) String userId,
        @RequestParam("userName") @Pattern(regexp = REG_USER_NAME) String userName,
        @ApiParam(value = "test yaml files", required = true) @RequestPart("file") List<MultipartFile> uploadFiles)
            throws IOException {
        testCaseServiceFacade.testCaseUpload(new User(userId, userName), uploadFiles);
        return ResponseEntity.ok("upload test case success.");
    }

    @GetMapping(value = "/testcases", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "get all test cases.", response = TestCaseDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "microservice not found", response = String.class),
        @ApiResponse(code = 415, message = "Unprocessable " + "MicroServiceInfo Entity ", response = String.class),
        @ApiResponse(code = 500, message = "resource grant " + "error", response = String.class)
    })
    @PreAuthorize("hasRole('APPSTORE_TENANT')")
    public ResponseEntity<List<TestCaseDto>> queryAllTestCases() {
        return testCaseServiceFacade.queryAll();
    }

    @GetMapping(value = "/testcases/{testCaseId}", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "get test case list by test case id.", response = TestCaseDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "microservice not found", response = String.class),
            @ApiResponse(code = 415, message = "Unprocessable " + "MicroServiceInfo Entity ", response = String.class),
            @ApiResponse(code = 500, message = "resource grant " + "error", response = String.class)
    })
    @PreAuthorize("hasRole('APPSTORE_TENANT')")
    public ResponseEntity<TestCaseDto> queryTestCaseById(
            @ApiParam(value = "case id") @PathVariable("testCaseId") @Pattern(regexp = REG_ID) String testCaseId) {
        return testCaseServiceFacade.queryByTestCaseId(testCaseId);
    }

    @DeleteMapping(value = "/testcases/{testCaseId}", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "delete test case list by test case id.", response = String.class)
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "microservice not found", response = String.class),
        @ApiResponse(code = 415, message = "Unprocessable " + "MicroServiceInfo Entity ", response = String.class),
        @ApiResponse(code = 500, message = "resource grant " + "error", response = String.class)
    })
    @PreAuthorize("hasRole('APPSTORE_TENANT')")
    public ResponseEntity<String> deleteTestCase(
        @RequestParam("userId") @Pattern(regexp = REG_USER_ID) String userId,
        @RequestParam("userName") @Pattern(regexp = REG_USER_NAME) String userName,
        @ApiParam(value = "testCase id") @PathVariable("testCaseId") @Pattern(regexp = REG_ID) String testCaseId) {
        testCaseServiceFacade.deleteTestCase(testCaseId, new User(userId, userName));
        return new ResponseEntity<>("delete App success.", HttpStatus.OK);
    }
}
