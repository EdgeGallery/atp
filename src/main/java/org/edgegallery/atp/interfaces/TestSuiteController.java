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
import javax.validation.constraints.Size;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.model.testscenario.TestScenarioIds;
import org.edgegallery.atp.model.testsuite.TestSuite;
import org.edgegallery.atp.service.TestSuiteService;
import org.edgegallery.atp.utils.CommonUtil;
import org.hibernate.validator.constraints.Length;
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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
@RestSchema(schemaId = "testSuite")
@RequestMapping("/edgegallery/atp/v1")
@Api(tags = {"APT Test Suite Controller"})
@Validated
public class TestSuiteController {
    private static final String REG_ID = "[0-9a-f]{8}(-[0-9a-f]{4}){3}-[0-9a-f]{12}";

    @Autowired
    TestSuiteService testSuiteService;

    @PostMapping(value = "/testsuites", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "create test suite.", response = String.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = "microservice not found", response = String.class),
            @ApiResponse(code = 500, message = "resource grant error", response = String.class)})
    @PreAuthorize("hasRole('ATP_ADMIN')")
    public ResponseEntity<TestSuite> createTestSuite(
            @ApiParam(value = "test suite chinese name",
                    required = true) @Size(max = Constant.LENGTH_64) @RequestParam("nameCh") String nameCh,
            @ApiParam(value = "test suite english name",
                    required = true) @Size(max = Constant.LENGTH_64) @RequestParam("nameEn") String nameEn,
            @ApiParam(value = "test suite chinese description",
                    required = true) @Size(
                            max = Constant.LENGTH_255) @RequestParam("descriptionCh") String descriptionCh,
            @ApiParam(value = "test suite english description",
                    required = true) @Size(
                            max = Constant.LENGTH_255) @RequestParam("descriptionEn") String descriptionEn,
            @ApiParam(value = "test scenario it belongs to",
                    required = true) @Size(
                            max = Constant.LENGTH_255) @RequestParam("scenarioIdList") List<String> scenarioIdList) {
        TestSuite testSuite = TestSuite.builder().setId(CommonUtil.generateId()).setDescriptionEn(descriptionEn)
                .setdescriptionCh(descriptionCh).setNameEn(nameEn).setnameCh(nameCh).build();
        testSuite.setScenarioIdList(scenarioIdList);
        return ResponseEntity.ok(testSuiteService.createTestSuite(testSuite));
    }

    @PutMapping(value = "/testsuites/{id}", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "modify test suite.", response = TestSuite.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = "microservice not found", response = String.class),
            @ApiResponse(code = 500, message = "resource grant " + "error", response = String.class)})
    @PreAuthorize("hasRole('ATP_ADMIN')")
    public ResponseEntity<TestSuite> updateTestSuite(
            @ApiParam(value = "test suite id") @PathVariable("id") @Pattern(regexp = REG_ID) String id,
            @ApiParam(value = "test suite chinese name", required = false)@Size(max = Constant.LENGTH_64) @RequestParam("nameCh") String nameCh,
            @ApiParam(value = "test suite english name",
                    required = false) @Size(max = Constant.LENGTH_64) @RequestParam("nameEn") String nameEn,
            @ApiParam(value = "test suite chinese description",
                    required = false) @Size(
                            max = Constant.LENGTH_255) @RequestParam("descriptionCh") String descriptionCh,
            @ApiParam(value = "test suite english description",
                    required = false) @Size(
                            max = Constant.LENGTH_255) @RequestParam("descriptionEn") String descriptionEn,
            @ApiParam(value = "test scenario id list belongs to test suite",
                    required = false) @Size(
                            max = Constant.LENGTH_255) @RequestParam("scenarioIdList") List<String> scenarioIdList) {
        TestSuite testSuite = TestSuite.builder().setId(id).setDescriptionEn(descriptionEn)
                .setdescriptionCh(descriptionCh).setNameEn(nameEn).setnameCh(nameCh).build();
        testSuite.setScenarioIdList(CollectionUtils.isEmpty(scenarioIdList) ? null : scenarioIdList);
        return ResponseEntity.ok(testSuiteService.updateTestSuite(testSuite));
    }

    @DeleteMapping(value = "/testsuites/{id}", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "delete test suite.", response = Boolean.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = "microservice not found", response = String.class),
            @ApiResponse(code = 500, message = "resource grant " + "error", response = String.class)})
    @PreAuthorize("hasRole('ATP_ADMIN')")
    public ResponseEntity<Boolean> deleteTestSuite(
            @ApiParam(value = "test suite id") @PathVariable("id") @Pattern(regexp = REG_ID) String id) {
        return ResponseEntity.ok(testSuiteService.deleteTestSuite(id));
    }

    @GetMapping(value = "/testsuites/{id}", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "get one test suite.", response = TestSuite.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = "microservice not found", response = String.class),
            @ApiResponse(code = 500, message = "resource grant " + "error", response = String.class)})
    @PreAuthorize("hasRole('ATP_GUEST') || hasRole('ATP_TENANT') || hasRole('ATP_ADMIN')")
    public ResponseEntity<TestSuite> queryTestSuite(
            @ApiParam(value = "test suite id") @PathVariable("id") @Pattern(regexp = REG_ID) String id) {
        return ResponseEntity.ok(testSuiteService.getTestSuite(id));
    }

    @GetMapping(value = "/testsuites", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "get all test suites.", response = TestSuite.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = "microservice not found", response = String.class),
            @ApiResponse(code = 500, message = "resource grant " + "error", response = String.class)})
    @PreAuthorize("hasRole('ATP_GUEST') || hasRole('ATP_TENANT') || hasRole('ATP_ADMIN')")
    public ResponseEntity<List<TestSuite>> queryAllTestSuite(
            @ApiParam(value = "locale language") @Length(max = Constant.LENGTH_64) @QueryParam("locale") String locale,
            @ApiParam(value = "test Suite name") @Length(max = Constant.LENGTH_64) @QueryParam("name") String name,
            @ApiParam(
                    value = "test scenario id list belongs to test suite") @QueryParam("scenarioIdList") TestScenarioIds scenarioIdList) {
        return ResponseEntity.ok(testSuiteService.queryAllTestSuite(locale, name, scenarioIdList.getScenarioIdList()));
    }
}

