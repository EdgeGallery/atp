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
import org.edgegallery.atp.model.testscenario.TestScenarioIds;
import org.edgegallery.atp.model.testsuite.TestSuite;
import org.edgegallery.atp.service.TestSuiteService;
import org.edgegallery.atp.utils.CommonUtil;
import org.edgegallery.atp.utils.exception.FileNotExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RestSchema(schemaId = "testSuiteV2")
@RequestMapping("/edgegallery/atp/v2")
@Api(tags = {"APT Test Suite ControllerV2"})
@Validated
public class TestSuiteControllerV2 {

    @Autowired
    TestSuiteService testSuiteService;

    /**
     * create test suite.
     *
     * @param nameCh nameCh
     * @param nameEn nameEn
     * @param descriptionCh descriptionCh
     * @param descriptionEn descriptionEn
     * @param scenarioIdList scenarioIdList
     * @return test suite
     */
    @PostMapping(value = "/testsuites", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "create test suite.", response = String.class)
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "microservice not found", response = String.class),
        @ApiResponse(code = 500, message = "resource grant error", response = String.class)
    })
    @PreAuthorize("hasRole('ATP_ADMIN')")
    public ResponseEntity<ResponseObject<TestSuite>> createTestSuite(
        @ApiParam(value = "test suite chinese name", required = true) @Size(max = Constant.LENGTH_64)
        @RequestParam("nameCh") String nameCh,
        @ApiParam(value = "test suite english name", required = true) @Size(max = Constant.LENGTH_64)
        @RequestParam("nameEn") String nameEn,
        @ApiParam(value = "test suite chinese description", required = true) @Size(max = Constant.LENGTH_255)
        @RequestParam("descriptionCh") String descriptionCh,
        @ApiParam(value = "test suite english description", required = true) @Size(max = Constant.LENGTH_255)
        @RequestParam("descriptionEn") String descriptionEn,
        @ApiParam(value = "test scenario it belongs to", required = true) @Size(max = Constant.LENGTH_255)
        @RequestParam("scenarioIdList") List<String> scenarioIdList) {
        TestSuite testSuite = TestSuite.builder().setId(CommonUtil.generateId()).setDescriptionEn(descriptionEn)
            .setDescriptionCh(descriptionCh).setNameEn(nameEn).setNameCh(nameCh).build();
        testSuite.setScenarioIdList(scenarioIdList);

        ResponseObject<TestSuite> result = new ResponseObject<TestSuite>(testSuiteService.createTestSuite(testSuite),
            ErrorCode.RET_CODE_SUCCESS, null, "create test suite successfully.");
        return ResponseEntity.ok(result);
    }

    /**
     * update test suite.
     *
     * @param id id
     * @param nameCh nameCh
     * @param nameEn nameEn
     * @param descriptionCh descriptionCh
     * @param descriptionEn descriptionEn
     * @param scenarioIdList scenarioIdList
     * @return test suite info
     */
    @PutMapping(value = "/testsuites/{id}", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "modify test suite.", response = TestSuite.class)
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "microservice not found", response = String.class),
        @ApiResponse(code = 500, message = "resource grant " + "error", response = String.class)
    })
    @PreAuthorize("hasRole('ATP_ADMIN')")
    public ResponseEntity<ResponseObject<TestSuite>> updateTestSuite(
        @ApiParam(value = "test suite id") @PathVariable("id") @Pattern(regexp = Constant.REG_ID) String id,
        @ApiParam(value = "test suite chinese name", required = false) @Size(max = Constant.LENGTH_64)
        @RequestParam("nameCh") String nameCh,
        @ApiParam(value = "test suite english name", required = false) @Size(max = Constant.LENGTH_64)
        @RequestParam("nameEn") String nameEn,
        @ApiParam(value = "test suite chinese description", required = false) @Size(max = Constant.LENGTH_255)
        @RequestParam("descriptionCh") String descriptionCh,
        @ApiParam(value = "test suite english description", required = false) @Size(max = Constant.LENGTH_255)
        @RequestParam("descriptionEn") String descriptionEn,
        @ApiParam(value = "test scenario id list belongs to test suite", required = false)
        @Size(max = Constant.LENGTH_255) @RequestParam("scenarioIdList") List<String> scenarioIdList) {
        TestSuite testSuite = TestSuite.builder().setId(id).setDescriptionEn(descriptionEn)
            .setDescriptionCh(descriptionCh).setNameEn(nameEn).setNameCh(nameCh).build();
        testSuite.setScenarioIdList(CollectionUtils.isEmpty(scenarioIdList) ? null : scenarioIdList);

        ResponseObject<TestSuite> result = new ResponseObject<TestSuite>(testSuiteService.updateTestSuite(testSuite),
            ErrorCode.RET_CODE_SUCCESS, null, "update test suite successfully.");
        return ResponseEntity.ok(result);
    }

    /**
     * query test suite.
     *
     * @param id id
     * @return test suite
     * @throws FileNotFoundException FileNotFoundException
     */
    @GetMapping(value = "/testsuites/{id}", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "get one test suite.", response = TestSuite.class)
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "microservice not found", response = String.class),
        @ApiResponse(code = 500, message = "resource grant " + "error", response = String.class)
    })
    @PreAuthorize("hasRole('ATP_GUEST') || hasRole('ATP_TENANT') || hasRole('ATP_ADMIN')")
    public ResponseEntity<ResponseObject<TestSuite>> queryTestSuite(
        @ApiParam(value = "test suite id") @PathVariable("id") @Pattern(regexp = Constant.REG_ID) String id)
        throws FileNotExistsException {
        ResponseObject<TestSuite> result = new ResponseObject<TestSuite>(testSuiteService.getTestSuite(id),
            ErrorCode.RET_CODE_SUCCESS, null, "get test suite by id successfully.");
        return ResponseEntity.ok(result);
    }

    /**
     * query all test suite.
     *
     * @param locale locale
     * @param name name
     * @param scenarioIdList scenarioIdList
     * @return test suite info
     */
    @GetMapping(value = "/testsuites", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "get all test suites.", response = TestSuite.class)
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "microservice not found", response = String.class),
        @ApiResponse(code = 500, message = "resource grant " + "error", response = String.class)
    })
    @PreAuthorize("hasRole('ATP_GUEST') || hasRole('ATP_TENANT') || hasRole('ATP_ADMIN')")
    public ResponseEntity<PageResult<TestSuite>> queryAllTestSuite(
        @ApiParam(value = "locale language") @Size(max = Constant.LENGTH_64) @QueryParam("locale") String locale,
        @ApiParam(value = "test Suite name") @Size(max = Constant.LENGTH_64) @QueryParam("name") String name,
        @ApiParam(value = "test scenario id list belongs to test suite") @QueryParam("scenarioIdList")
            TestScenarioIds scenarioIdList, @ApiParam(value = "limit") @QueryParam("limit") @NotNull int limit,
        @ApiParam(value = "offset") @QueryParam("offset") @NotNull int offset) {
        return ResponseEntity.ok(testSuiteService
            .queryAllTestSuiteByPagination(locale, name, scenarioIdList.getScenarioIdList(), limit, offset));
    }

    /**
     * delete test suite.
     *
     * @param id id
     * @return true
     */
    @DeleteMapping(value = "/testsuites/{id}", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "delete test suite.", response = Boolean.class)
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "microservice not found", response = String.class),
        @ApiResponse(code = 500, message = "resource grant " + "error", response = String.class)
    })
    @PreAuthorize("hasRole('ATP_ADMIN')")
    public ResponseEntity<Boolean> deleteTestSuite(
        @ApiParam(value = "test suite id") @PathVariable("id") @Pattern(regexp = Constant.REG_ID) String id) {
        return ResponseEntity.ok(testSuiteService.deleteTestSuite(id));
    }
}

