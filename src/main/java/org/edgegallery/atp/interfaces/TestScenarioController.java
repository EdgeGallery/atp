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
import org.edgegallery.atp.model.testscenario.TestScenario;
import org.edgegallery.atp.service.TestScenarioService;
import org.edgegallery.atp.utils.CommonUtil;
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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
@RestSchema(schemaId = "testScenario")
@RequestMapping("/edgegallery/atp/v1")
@Api(tags = {"APT Test Case Controller"})
@Validated
public class TestScenarioController {
    private static final String REG_ID = "[0-9a-f]{8}(-[0-9a-f]{4}){3}-[0-9a-f]{12}";

    @Autowired
    TestScenarioService testScenarioService;

    @PostMapping(value = "/testscenarios", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "create test scenario.", response = String.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = "microservice not found", response = String.class),
            @ApiResponse(code = 500, message = "resource grant error", response = String.class)})
    @PreAuthorize("hasRole('ATP_TENANT')")
    public ResponseEntity<TestScenario> createTestScenario(
            @ApiParam(value = "test scenario chinese name", required = true) @RequestParam("nameCh") String nameCh,
            @ApiParam(value = "test scenario english name", required = true) @RequestParam("nameEn") String nameEn,
            @ApiParam(value = "test scenario chinese description",
                    required = true) @RequestParam("descriptionCh") String descriptionZn,
            @ApiParam(value = "test scenario english description",
                    required = true) @RequestParam("descriptionEn") String descriptionEn,
            @ApiParam(value = "test scenario icon", required = true) @RequestPart("icon") MultipartFile icon) {
        TestScenario testScenario =
                TestScenario.builder().setId(CommonUtil.generateId()).setDescriptionEn(descriptionEn)
                .setdescriptionCh(descriptionZn).setNameEn(nameEn).setnameCh(nameCh).build();
        return ResponseEntity.ok(testScenarioService.createTestScenario(testScenario, icon));
    }

    @PutMapping(value = "/testscenarios/{id}", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "modify test scenario.", response = TestScenario.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = "microservice not found", response = String.class),
            @ApiResponse(code = 500, message = "resource grant " + "error", response = String.class)})
    @PreAuthorize("hasRole('ATP_TENANT')")
    public ResponseEntity<TestScenario> updateTestScenario(
            @ApiParam(value = "test scenario id") @PathVariable("id") @Pattern(regexp = REG_ID) String id,
            @ApiParam(value = "test scenario chinese name", required = false) @RequestParam("nameCh") String nameCh,
            @ApiParam(value = "test scenario english name", required = false) @RequestParam("nameEn") String nameEn,
            @ApiParam(value = "test scenario chinese description",
                    required = false) @RequestParam("descriptionCh") String descriptionZn,
            @ApiParam(value = "test scenario english description",
                    required = false) @RequestParam("descriptionEn") String descriptionEn,
            @ApiParam(value = "test scenario icon", required = false) @RequestPart("icon") MultipartFile icon) {
        TestScenario testScenario = TestScenario.builder().setId(id).setDescriptionEn(descriptionEn)
                .setdescriptionCh(descriptionZn).setNameEn(nameEn).setnameCh(nameCh).build();
        return ResponseEntity.ok(testScenarioService.updateTestScenario(testScenario, icon));
    }

    @DeleteMapping(value = "/testscenarios/{id}", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "delete test scenario.", response = Boolean.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = "microservice not found", response = String.class),
            @ApiResponse(code = 500, message = "resource grant " + "error", response = String.class)})
    @PreAuthorize("hasRole('ATP_TENANT')")
    public ResponseEntity<Boolean> deleteTestScenario(
            @ApiParam(value = "test scenario id") @PathVariable("id") @Pattern(regexp = REG_ID) String id) {
        return ResponseEntity.ok(testScenarioService.deleteTestScenario(id));
    }

    @GetMapping(value = "/testscenarios/{id}", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "get one test scenario.", response = TestScenario.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = "microservice not found", response = String.class),
            @ApiResponse(code = 500, message = "resource grant " + "error", response = String.class)})
    @PreAuthorize("hasRole('ATP_TENANT')")
    public ResponseEntity<TestScenario> queryTestScenario(
            @ApiParam(value = "test scenario id") @PathVariable("id") @Pattern(regexp = REG_ID) String id) {
        return ResponseEntity.ok(testScenarioService.getTestScenario(id));
    }

    @GetMapping(value = "/testscenarios", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "get all test scenarios.", response = TestScenario.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = "microservice not found", response = String.class),
            @ApiResponse(code = 500, message = "resource grant " + "error", response = String.class)})
    @PreAuthorize("hasRole('ATP_TENANT')")
    public ResponseEntity<List<TestScenario>> queryAllTestScenario(
            @ApiParam(value = "locale language") @QueryParam("locale") String locale,
            @ApiParam(value = "test scenario name") @QueryParam("name") String name) {
        return ResponseEntity.ok(testScenarioService.queryAllTestScenario(locale, name));
    }
}

