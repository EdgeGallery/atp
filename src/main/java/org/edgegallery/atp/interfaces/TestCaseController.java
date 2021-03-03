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
import org.edgegallery.atp.model.testcase.TestCase;
import org.edgegallery.atp.model.testsuite.TestSuiteIdList;
import org.edgegallery.atp.service.TestCaseService;
import org.edgegallery.atp.utils.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
@RestSchema(schemaId = "testCase")
@RequestMapping("/edgegallery/atp/v1")
@Api(tags = {"APT Test Case Controller"})
@Validated
public class TestCaseController {

    @Autowired
    private TestCaseService testCaseService;

    private static final String REG_ID = "[0-9a-f]{8}(-[0-9a-f]{4}){3}-[0-9a-f]{12}";

    /**
     * get all tasks according userId
     * 
     * @param userId userId
     * @return task list
     */
    @GetMapping(value = "/testcases", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "get all test cases.", response = TestCase.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = "microservice not found", response = String.class),
            @ApiResponse(code = 500, message = "resource grant " + "error", response = String.class)})
    @PreAuthorize("hasRole('ATP_GUEST') || hasRole('ATP_TENANT') || hasRole('ATP_ADMIN')")
    public ResponseEntity<List<TestCase>> getAllTestCases(@QueryParam("type") String type,
            @QueryParam("locale") String locale, @QueryParam("name") String name,
            @QueryParam("testSuiteIdList") TestSuiteIdList testSuiteIds) {
        CommonUtil.lengthCheck(type);
        CommonUtil.lengthCheck(locale);
        CommonUtil.lengthCheck(name);
        return testCaseService.getAllTestCases(type, locale, name, testSuiteIds.getTestSuiteIdList());
    }

    @PostMapping(value = "/testcases", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "create test case.", response = TestCase.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = "microservice not found", response = String.class),
            @ApiResponse(code = 500, message = "resource grant " + "error", response = String.class)})
    @PreAuthorize("hasRole('ATP_TENANT') || hasRole('ATP_ADMIN')")
    public ResponseEntity<TestCase> createTestCase(
            @ApiParam(value = "test case file", required = true) @RequestPart("file") MultipartFile file,
            @ApiParam(value = "test case chinese name", required = true) @RequestParam("nameCh") String nameCh,
            @ApiParam(value = "test case english name", required = true) @RequestParam("nameEn") String nameEn,
            @ApiParam(value = "test case type", required = true) @RequestParam("type") String type,
            @ApiParam(value = "test case chinese description",
                    required = true) @RequestParam("descriptionCh") String descriptionCh,
            @ApiParam(value = "test case english description",
            required = true) @RequestParam("descriptionEn") String descriptionEn,
            @ApiParam(value = "test case code language",
                    required = true) @RequestParam("codeLanguage") String codeLanguage,
            @ApiParam(value = "test case expect result in chinese",
                    required = true) @RequestParam("expectResultCh") String expectResultCh,
            @ApiParam(value = "test case expect result in english",
            required = true) @RequestParam("expectResultEn") String expectResultEn,
            @ApiParam(value = "test case expect result in chinese",
            required = true) @RequestParam("testStepCh") String testStepCh,
            @ApiParam(value = "test case expect result in english",
                    required = true) @RequestParam("testStepEn") String testStepEn,
            @ApiParam(value = "test suite list the test case belong to",
                    required = true) @RequestParam("testSuiteIdList") List<String> testSuiteIds) {
        TestCase testCase = TestCase.builder().setId(CommonUtil.generateId()).setCodeLanguage(codeLanguage)
                .setdescriptionCh(descriptionCh).setDescriptionEn(descriptionEn).setExpectResultCh(expectResultCh)
                .setExpectResultEn(expectResultEn).setNameCh(nameCh).setNameEn(nameEn).setTestStepCh(testStepCh)
                .setTestStepEn(testStepEn).setType(type).build().toTestCase();
        testCase.setTestSuiteIdList(testSuiteIds);
        return ResponseEntity.ok(testCaseService.createTestCase(file, testCase));
    }

    @PutMapping(value = "/testcases", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "modify test case.", response = TestCase.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = "microservice not found", response = String.class),
            @ApiResponse(code = 500, message = "resource grant " + "error", response = String.class)})
    @PreAuthorize("hasRole('ATP_TENANT') || hasRole('ATP_ADMIN')")
    public ResponseEntity<TestCase> updateTestCase(
            @ApiParam(value = "test case id", required = true) @RequestParam("id") String id,
            @ApiParam(value = "test case file", required = false) @RequestPart("file") MultipartFile file,
            @ApiParam(value = "test case chinese description",
                    required = false) @RequestParam("descriptionCh") String descriptionCh,
            @ApiParam(value = "test case english description",
                    required = false) @RequestParam("descriptionEn") String descriptionEn,
            @ApiParam(value = "test case code language",
                    required = false) @RequestParam("codeLanguage") String codeLanguage,
            @ApiParam(value = "test case expect result in chinese",
                    required = false) @RequestParam("expectResultCh") String expectResultCh,
            @ApiParam(value = "test case expect result in english",
                    required = false) @RequestParam("expectResultEn") String expectResultEn,
            @ApiParam(value = "test case test step in chinese",
                    required = false) @RequestParam(value = "testStepCh") String testStepCh,
            @ApiParam(value = "test case test step in english",
                    required = false) @RequestParam(value = "testStepEn") String testStepEn,
            @ApiParam(value = "test suite list the test case belong to",
                    required = false) @RequestParam("testSuiteIdList") List<String> testSuiteIds) {
        TestCase testCase = TestCase.builder().setId(id).setCodeLanguage(codeLanguage)
                .setdescriptionCh(descriptionCh).setDescriptionEn(descriptionEn).setExpectResultCh(expectResultCh)
                .setExpectResultEn(expectResultEn).setTestStepCh(testStepCh).setTestStepEn(testStepEn).build()
                .toTestCase();
        testCase.setTestSuiteIdList(testSuiteIds);
        return ResponseEntity.ok(testCaseService.updateTestCase(file, testCase));
    }

    @DeleteMapping(value = "/testcases/{id}", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "delete test case.", response = Boolean.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = "microservice not found", response = String.class),
            @ApiResponse(code = 500, message = "resource grant " + "error", response = String.class)})
    @PreAuthorize("hasRole('ATP_TENANT') || hasRole('ATP_ADMIN')")
    public ResponseEntity<Boolean> deleteTestCase(
            @ApiParam(value = "test case id") @PathVariable("id") @Pattern(regexp = REG_ID) String id) {
        return ResponseEntity.ok(testCaseService.deleteTestCase(id));
    }

    @GetMapping(value = "/testcases/{id}", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "get one test case.", response = TestCase.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = "microservice not found", response = String.class),
            @ApiResponse(code = 500, message = "resource grant " + "error", response = String.class)})
    @PreAuthorize("hasRole('ATP_GUEST') || hasRole('ATP_TENANT') || hasRole('ATP_ADMIN')")
    public ResponseEntity<TestCase> queryTestCase(
            @ApiParam(value = "test case id") @PathVariable("id") @Pattern(regexp = REG_ID) String id) {
        return ResponseEntity.ok(testCaseService.getTestCase(id));
    }

    @GetMapping(value = "/testcases/{id}/action/download", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "download test case", response = InputStreamResource.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = "microservice not found", response = String.class),
            @ApiResponse(code = 500, message = "resource grant error", response = String.class)})
    @PreAuthorize("hasRole('ATP_TENANT') || hasRole('ATP_ADMIN')")
    public ResponseEntity<InputStreamResource> downloadTestCase(
            @ApiParam(value = "test case id") @PathVariable("id") @Pattern(regexp = REG_ID) String id) {
        return testCaseService.downloadTestCase(id);
    }
}
