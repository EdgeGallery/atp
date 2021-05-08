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

package org.edgegallery.atp.interfaces;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.io.FileNotFoundException;
import java.util.List;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.model.BatchOpsRes;
import org.edgegallery.atp.model.testscenario.TestScenario;
import org.edgegallery.atp.model.testscenario.testcase.AllTestScenarios;
import org.edgegallery.atp.service.TestScenarioService;
import org.edgegallery.atp.utils.CommonUtil;
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
@RestSchema(schemaId = "testScenario")
@RequestMapping("/edgegallery/atp/v1")
@Api(tags = {"APT Test Scenario Controller"})
@Validated
public class TestScenarioController {

    @Autowired
    TestScenarioService testScenarioService;

    /**
     * create test scenario.
     * 
     * @param nameCh nameCh
     * @param nameEn nameEn
     * @param descriptionZn descriptionZn
     * @param descriptionEn descriptionEn
     * @param icon icon
     * @return test scenario
     */
    @PostMapping(value = "/testscenarios", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "create test scenario.", response = String.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = "microservice not found", response = String.class),
            @ApiResponse(code = 500, message = "resource grant error", response = String.class)})
    @PreAuthorize("hasRole('ATP_ADMIN')")
    public ResponseEntity<TestScenario> createTestScenario(
            @ApiParam(value = "test scenario chinese name",
                    required = true) @Size(max = Constant.LENGTH_64) @RequestParam("nameCh") String nameCh,
            @ApiParam(value = "test scenario english name",
                    required = true) @Size(max = Constant.LENGTH_64) @RequestParam("nameEn") String nameEn,
            @ApiParam(value = "test scenario chinese description",
                    required = true) @Size(
                            max = Constant.LENGTH_255) @RequestParam("descriptionCh") String descriptionZn,
            @ApiParam(value = "test scenario english description",
                    required = true) @Size(
                            max = Constant.LENGTH_255) @RequestParam("descriptionEn") String descriptionEn,
            @ApiParam(value = "test scenario icon", required = true) @RequestPart("icon") MultipartFile icon) {
        TestScenario testScenario =
                TestScenario.builder().setId(CommonUtil.generateId()).setDescriptionEn(descriptionEn)
                        .setdescriptionCh(descriptionZn).setNameEn(nameEn).setnameCh(nameCh).build();
        return ResponseEntity.ok(testScenarioService.createTestScenario(testScenario, icon));
    }

    /**
     * update test scerario.
     * 
     * @param id id
     * @param nameCh nameCh
     * @param nameEn nameEn
     * @param descriptionZn descriptionZn
     * @param descriptionEn descriptionEn
     * @param icon icon
     * @return test scenario
     */
    @PutMapping(value = "/testscenarios/{id}", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "modify test scenario.", response = TestScenario.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = "microservice not found", response = String.class),
            @ApiResponse(code = 500, message = "resource grant " + "error", response = String.class)})
    @PreAuthorize("hasRole('ATP_ADMIN')")
    public ResponseEntity<TestScenario> updateTestScenario(
            @ApiParam(value = "test scenario id") @PathVariable("id") @Pattern(regexp = Constant.REG_ID) String id,
            @ApiParam(value = "test scenario chinese name",
                    required = false) @Size(max = Constant.LENGTH_64) @RequestParam("nameCh") String nameCh,
            @ApiParam(value = "test scenario english name",
                    required = false) @Size(max = Constant.LENGTH_64) @RequestParam("nameEn") String nameEn,
            @ApiParam(value = "test scenario chinese description",
                    required = false) @Size(
                            max = Constant.LENGTH_255) @RequestParam("descriptionCh") String descriptionZn,
            @ApiParam(value = "test scenario english description",
                    required = false) @Size(
                            max = Constant.LENGTH_255) @RequestParam("descriptionEn") String descriptionEn,
            @ApiParam(value = "test scenario icon", required = false) @RequestPart("icon") MultipartFile icon) {
        TestScenario testScenario = TestScenario.builder().setId(id).setDescriptionEn(descriptionEn)
                .setdescriptionCh(descriptionZn).setNameEn(nameEn).setnameCh(nameCh).build();
        return ResponseEntity.ok(testScenarioService.updateTestScenario(testScenario, icon));
    }

    /**
     * delete test scenario.
     * 
     * @param id id
     * @return true
     */
    @DeleteMapping(value = "/testscenarios/{id}", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "delete test scenario.", response = Boolean.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = "microservice not found", response = String.class),
            @ApiResponse(code = 500, message = "resource grant " + "error", response = String.class)})
    @PreAuthorize("hasRole('ATP_ADMIN')")
    public ResponseEntity<Boolean> deleteTestScenario(
            @ApiParam(value = "test scenario id") @PathVariable("id") @Pattern(regexp = Constant.REG_ID) String id) {
        return ResponseEntity.ok(testScenarioService.deleteTestScenario(id));
    }

    /**
     * query test scenario.
     * 
     * @param id id
     * @return test scenario
     * @throws FileNotFoundException FileNotFoundException
     */
    @GetMapping(value = "/testscenarios/{id}", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "get one test scenario.", response = TestScenario.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = "microservice not found", response = String.class),
            @ApiResponse(code = 500, message = "resource grant " + "error", response = String.class)})
    @PreAuthorize("hasRole('ATP_GUEST') || hasRole('ATP_TENANT') || hasRole('ATP_ADMIN')")
    public ResponseEntity<TestScenario> queryTestScenario(
            @ApiParam(value = "test scenario id") @PathVariable("id") @Pattern(regexp = Constant.REG_ID) String id)
            throws FileNotFoundException {
        return ResponseEntity.ok(testScenarioService.getTestScenario(id));
    }

    /**
     * query all test scenario.
     * 
     * @param locale locale
     * @param name name
     * @return test scenario list
     */
    @GetMapping(value = "/testscenarios", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "get all test scenarios.", response = TestScenario.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = "microservice not found", response = String.class),
            @ApiResponse(code = 500, message = "resource grant " + "error", response = String.class)})
    @PreAuthorize("hasRole('ATP_GUEST') || hasRole('ATP_TENANT') || hasRole('ATP_ADMIN')")
    public ResponseEntity<List<TestScenario>> queryAllTestScenario(
            @ApiParam(value = "locale language") @Length(max = Constant.LENGTH_64) @QueryParam("locale") String locale,
            @ApiParam(value = "test scenario name") @Length(max = Constant.LENGTH_64) @QueryParam("name") String name) {
        return ResponseEntity.ok(testScenarioService.queryAllTestScenario(locale, name));
    }

    /**
     * get test cases by scenarioIds.
     * 
     * @param ids ids
     * @return test scenario lists.
     */
    @PostMapping(value = "/testscenarios/testcases", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "get all test cases belonged to special test scenario id list.", response = String.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = "microservice not found", response = String.class),
            @ApiResponse(code = 500, message = "resource grant error", response = String.class)})
    @PreAuthorize("hasRole('ATP_GUEST') || hasRole('ATP_TENANT') || hasRole('ATP_ADMIN')")
    public ResponseEntity<List<AllTestScenarios>> getTestCasesByScenarioIds(@ApiParam(value = "test scenario id list",
            required = true) @RequestParam("scenarioIds") List<String> ids) {
        return ResponseEntity.ok(testScenarioService.getTestCasesByScenarioIds(ids));
    }

    @PostMapping(value = "/testmodels/action/import", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "import test model.", response = String.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = "microservice not found", response = String.class),
            @ApiResponse(code = 500, message = "resource grant error", response = String.class)})
    @PreAuthorize("hasRole('ATP_ADMIN')")
    public ResponseEntity<BatchOpsRes> importTestModels(
            @ApiParam(value = "test model file", required = true) @RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok(testScenarioService.importTestModels(file));
    }
}

