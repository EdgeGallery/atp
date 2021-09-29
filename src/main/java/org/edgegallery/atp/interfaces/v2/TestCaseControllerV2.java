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

package org.edgegallery.atp.interfaces.v2;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.constant.ErrorCode;
import org.edgegallery.atp.model.PageResult;
import org.edgegallery.atp.model.ResponseObject;
import org.edgegallery.atp.model.testcase.TestCase;
import org.edgegallery.atp.model.testsuite.TestSuiteIdList;
import org.edgegallery.atp.service.TestCaseService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RestSchema(schemaId = "testCaseV2")
@RequestMapping("/edgegallery/atp/v2")
@Api(tags = {"ATP Test Case ControllerV2"})
@Validated
public class TestCaseControllerV2 {

    @Autowired
    private TestCaseService testCaseService;

    /**
     * create test cases.
     *
     * @param file file
     * @param nameCh nameCh
     * @param nameEn nameEn
     * @param type type
     * @param descriptionCh descriptionCh
     * @param descriptionEn descriptionEn
     * @param codeLanguage codeLanguage
     * @param expectResultCh expectResultCh
     * @param expectResultEn expectResultEn
     * @param testStepCh testStepCh
     * @param testStepEn testStepEn
     * @param testSuiteIds testSuiteIds
     * @return test case
     */
    @PostMapping(value = "/testcases", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "create test case.", response = TestCase.class)
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "microservice not found", response = String.class),
        @ApiResponse(code = 500, message = "resource grant " + "error", response = String.class)
    })
    @PreAuthorize("hasRole('ATP_ADMIN')")
    public ResponseEntity<ResponseObject<TestCase>> createTestCase(
        @ApiParam(value = "test case file", required = true) @RequestPart("file") MultipartFile file,
        @ApiParam(value = "test case chinese name", required = true) @Size(max = Constant.LENGTH_64)
        @RequestParam("nameCh") String nameCh,
        @ApiParam(value = "test case english name", required = true) @Size(max = Constant.LENGTH_64)
        @RequestParam("nameEn") String nameEn,
        @ApiParam(value = "test case type", required = true) @Size(max = Constant.LENGTH_64) @RequestParam("type")
            String type,
        @ApiParam(value = "test case chinese description", required = true) @Size(max = Constant.LENGTH_255)
        @RequestParam("descriptionCh") String descriptionCh,
        @ApiParam(value = "test case english description", required = true) @Size(max = Constant.LENGTH_255)
        @RequestParam("descriptionEn") String descriptionEn,
        @ApiParam(value = "test case code language", required = true) @Size(max = Constant.LENGTH_64)
        @RequestParam("codeLanguage") String codeLanguage,
        @ApiParam(value = "test case expect result in chinese", required = true) @Size(max = Constant.LENGTH_255)
        @RequestParam("expectResultCh") String expectResultCh,
        @ApiParam(value = "test case expect result in english", required = true) @Size(max = Constant.LENGTH_255)
        @RequestParam("expectResultEn") String expectResultEn,
        @ApiParam(value = "test case expect result in chinese", required = true) @Size(max = Constant.LENGTH_255)
        @RequestParam("testStepCh") String testStepCh,
        @ApiParam(value = "test case expect result in english", required = true) @Size(max = Constant.LENGTH_255)
        @RequestParam("testStepEn") String testStepEn,
        @ApiParam(value = "test suite list the test case belong to", required = true) @Size(max = Constant.LENGTH_255)
        @RequestParam("testSuiteIdList") List<String> testSuiteIds,
        @ApiParam(value = "config list the test case connected to", required = false) @Size(max = Constant.LENGTH_255)
        @RequestParam("configIdList") List<String> configIds) {
        TestCase testCase = new TestCase().builder().setId(CommonUtil.generateId()).setCodeLanguage(codeLanguage)
            .setDescriptionCh(descriptionCh).setDescriptionEn(descriptionEn).setExpectResultCh(expectResultCh)
            .setExpectResultEn(expectResultEn).setNameCh(nameCh).setNameEn(nameEn).setTestStepCh(testStepCh)
            .setTestStepEn(testStepEn).setType(type).build().toTestCase();
        testCase.setTestSuiteIdList(testSuiteIds);
        testCase.setConfigIdList(configIds);

        TestCase result = testCaseService.createTestCase(file, testCase);
        return ResponseEntity.ok(new ResponseObject<TestCase>(result, ErrorCode.RET_CODE_SUCCESS, null,
            "create test case successfully."));
    }

    /**
     * update Test Case.
     *
     * @param id id
     * @param file file
     * @param descriptionCh descriptionCh
     * @param descriptionEn descriptionEn
     * @param codeLanguage codeLanguage
     * @param expectResultCh expectResultCh
     * @param expectResultEn expectResultEn
     * @param testStepCh testStepCh
     * @param testStepEn testStepEn
     * @param testSuiteIds testSuiteIds
     * @return test case
     */
    @PutMapping(value = "/testcases", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "modify test case.", response = TestCase.class)
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "microservice not found", response = String.class),
        @ApiResponse(code = 500, message = "resource grant " + "error", response = String.class)
    })
    @PreAuthorize("hasRole('ATP_ADMIN')")
    public ResponseEntity<ResponseObject<TestCase>> updateTestCase(
        @ApiParam(value = "test case id", required = true) @RequestParam("id") String id,
        @ApiParam(value = "test case file", required = false) @RequestPart("file") MultipartFile file,
        @ApiParam(value = "test case chinese description", required = false) @Size(max = Constant.LENGTH_255)
        @RequestParam("descriptionCh") String descriptionCh,
        @ApiParam(value = "test case english description", required = false) @Size(max = Constant.LENGTH_255)
        @RequestParam("descriptionEn") String descriptionEn,
        @ApiParam(value = "test case code language", required = false) @Size(max = Constant.LENGTH_64)
        @RequestParam("codeLanguage") String codeLanguage,
        @ApiParam(value = "test case expect result in chinese", required = false) @Size(max = Constant.LENGTH_255)
        @RequestParam("expectResultCh") String expectResultCh,
        @ApiParam(value = "test case expect result in english", required = false) @Size(max = Constant.LENGTH_255)
        @RequestParam("expectResultEn") String expectResultEn,
        @ApiParam(value = "test case test step in chinese", required = false) @Size(max = Constant.LENGTH_255)
        @RequestParam(value = "testStepCh") String testStepCh,
        @ApiParam(value = "test case test step in english", required = false) @Size(max = Constant.LENGTH_255)
        @RequestParam(value = "testStepEn") String testStepEn,
        @ApiParam(value = "test suite list the test case belong to", required = false) @Size(max = Constant.LENGTH_255)
        @RequestParam("testSuiteIdList") List<String> testSuiteIds,
        @ApiParam(value = "config list the test case connected to", required = false) @Size(max = Constant.LENGTH_255)
        @RequestParam("configIdList") List<String> configIds) throws FileNotExistsException {
        TestCase testCase = new TestCase().builder().setId(id).setCodeLanguage(codeLanguage)
            .setDescriptionCh(descriptionCh).setDescriptionEn(descriptionEn).setExpectResultCh(expectResultCh)
            .setExpectResultEn(expectResultEn).setTestStepCh(testStepCh).setTestStepEn(testStepEn).build().toTestCase();
        testCase.setTestSuiteIdList(testSuiteIds);
        testCase.setConfigIdList(configIds);

        TestCase result = testCaseService.updateTestCase(file, testCase);
        return ResponseEntity.ok(new ResponseObject<TestCase>(result, ErrorCode.RET_CODE_SUCCESS, null,
            "update test case successfully."));
    }

    /**
     * query test case.
     *
     * @param id id
     * @return test case
     * @throws FileNotFoundException FileNotFoundException
     */
    @GetMapping(value = "/testcases/{id}", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "get one test case.", response = TestCase.class)
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "microservice not found", response = String.class),
        @ApiResponse(code = 500, message = "resource grant " + "error", response = String.class)
    })
    @PreAuthorize("hasRole('ATP_GUEST') || hasRole('ATP_TENANT') || hasRole('ATP_ADMIN')")
    public ResponseEntity<ResponseObject<TestCase>> queryTestCase(
        @ApiParam(value = "test case id") @PathVariable("id") @Pattern(regexp = Constant.REG_ID) String id)
        throws FileNotExistsException {
        TestCase result = testCaseService.getTestCase(id);
        return ResponseEntity.ok(new ResponseObject<TestCase>(result, ErrorCode.RET_CODE_SUCCESS, null,
            "query test case successfully."));
    }

    /**
     * get all testcases.
     *
     * @param type type
     * @param locale locale
     * @param name name
     * @param testSuiteIds testSuiteIds
     * @return test case list
     */
    @GetMapping(value = "/testcases", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "get all test cases.", response = TestCase.class)
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "microservice not found", response = String.class),
        @ApiResponse(code = 500, message = "resource grant " + "error", response = String.class)
    })
    @PreAuthorize("hasRole('ATP_GUEST') || hasRole('ATP_TENANT') || hasRole('ATP_ADMIN')")
    public ResponseEntity<PageResult<TestCase>> getAllTestCases(
        @QueryParam("type") @Length(max = Constant.LENGTH_64) String type,
        @QueryParam("locale") @Length(max = Constant.LENGTH_64) String locale,
        @QueryParam("name") @Length(max = Constant.LENGTH_64) String name,
        @QueryParam("testSuiteIdList") TestSuiteIdList testSuiteIds, @QueryParam("limit") @NotNull int limit,
        @QueryParam("offset") @NotNull int offset) {
        return testCaseService
            .getAllTestCasesByPagination(type, locale, name, testSuiteIds.getTestSuiteIdList(), limit, offset);
    }

    /**
     * download test case.
     *
     * @param id id
     * @return stream
     */
    @GetMapping(value = "/testcases/{id}/action/download", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "download test case", response = File.class)
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "microservice not found", response = String.class),
        @ApiResponse(code = 500, message = "resource grant error", response = String.class)
    })
    @PreAuthorize("hasRole('ATP_ADMIN')")
    public ResponseEntity<byte[]> downloadTestCase(
        @ApiParam(value = "test case id") @PathVariable("id") @Pattern(regexp = Constant.REG_ID) String id) {
        return testCaseService.downloadTestCase(id);
    }

    /**
     * delete test case.
     *
     * @param id id
     * @return true
     */
    @DeleteMapping(value = "/testcases/{id}", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "delete test case.", response = Boolean.class)
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "microservice not found", response = String.class),
        @ApiResponse(code = 500, message = "resource grant " + "error", response = String.class)
    })
    @PreAuthorize("hasRole('ATP_ADMIN')")
    public ResponseEntity<Boolean> deleteTestCase(
        @ApiParam(value = "test case id") @PathVariable("id") @Pattern(regexp = Constant.REG_ID) String id) {
        return ResponseEntity.ok(testCaseService.deleteTestCase(id));
    }
}
