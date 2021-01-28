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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @Autowired
    TestScenarioService testScenarioService;

    @PostMapping(value = "/testscenarios", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "create test scenario.", response = String.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = "microservice not found", response = String.class),
            @ApiResponse(code = 500, message = "resource grant error", response = String.class)})
    @PreAuthorize("hasRole('ATP_TENANT')")
    public ResponseEntity<TestScenario> createTestScenario(
            @ApiParam(value = "test scenario chinese name", required = true) @RequestParam("name_zh") String nameZh,
            @ApiParam(value = "test scenario english name", required = true) @RequestParam("name_en") String nameEn,
            @ApiParam(value = "test scenario chinese description",
                    required = true) @RequestParam("description_zh") String descriptionZn,
            @ApiParam(value = "test scenario english description",
                    required = true) @RequestParam("description_en") String descriptionEn) {
        TestScenario testScenario =
                TestScenario.builder().setId(CommonUtil.generateId()).setDescriptionEn(descriptionEn)
                .setDescriptionZh(descriptionZn).setNameEn(nameEn).setNameZh(nameZh).build();
        return ResponseEntity.ok(testScenarioService.creatTestScenario(testScenario));
    }
}
