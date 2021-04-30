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
import java.util.List;
import java.util.Map;
import javax.validation.constraints.Pattern;
import javax.ws.rs.core.MediaType;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.model.contribution.Contribution;
import org.edgegallery.atp.model.task.IdList;
import org.edgegallery.atp.service.ContributionService;
import org.edgegallery.atp.utils.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RestSchema(schemaId = "contribution")
@RequestMapping("/edgegallery/atp/v1")
@Api(tags = {"ATP Contribution Controller"})
@Validated
public class ContributionController {

    @Autowired
    ContributionService contributionService;

    /**
     * create test case contribution.
     * 
     * @param name name
     * @param objective objective
     * @param step step
     * @param expectResult expectResult
     * @param type type
     * @param file file
     * @return contribution info
     */
    @PostMapping(value = "/contributions", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "create test contribution.", response = String.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = "microservice not found", response = String.class),
            @ApiResponse(code = 500, message = "resource grant error", response = String.class)})
    @PreAuthorize("hasRole('ATP_TENANT') || hasRole('ATP_ADMIN')")
    public ResponseEntity<Contribution> createContribution(
            @ApiParam(value = "contribution name") @RequestParam("name") String name,
            @ApiParam(value = "contribution objective") @RequestParam("objective") String objective,
            @ApiParam(value = "contribution step") @RequestParam("step") String step,
            @ApiParam(value = "contribution expectResult") @RequestParam("expectResult") String expectResult,
            @ApiParam(value = "contribution type") @RequestParam("type") String type,
            @ApiParam(value = "script file", required = false) @RequestPart("file") MultipartFile file) {
        Contribution contribution = Contribution.builder().setId(CommonUtil.generateId()).setExpectResult(expectResult)
                .setName(name).setObjective(objective).setStep(step).setType(type).build();
        return ResponseEntity.ok(contributionService.createContribution(contribution, file));
    }

    /**
     * query all contribution.
     * 
     * @return contribution list
     */
    @GetMapping(value = "/contributions", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "get all contributions.", response = Contribution.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = "microservice not found", response = String.class),
            @ApiResponse(code = 500, message = "resource grant " + "error", response = String.class)})
    @PreAuthorize("hasRole('ATP_ADMIN')")
    public ResponseEntity<List<Contribution>> queryAllContribution() {
        return ResponseEntity.ok(contributionService.getAllContribution());
    }

    /**
     * batch delete contributions by contribution ids.
     * 
     * @param ids contributions ids
     * @return failed id list
     */
    @PostMapping(value = "/contributions/batch_delete", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "batch delete contributions", response = String.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = "microservice not found", response = String.class),
            @ApiResponse(code = 500, message = "resource grant " + "error", response = String.class)})
    @PreAuthorize("hasRole('ATP_ADMIN')")
    public ResponseEntity<Map<String, List<String>>> batchDelete(
            @ApiParam(value = "contribution id list") @RequestBody IdList ids) {
        return ResponseEntity.ok(contributionService.batchDelete(ids.getIds()));
    }

    @GetMapping(value = "/contributions/{id}/action/download", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "download contribution scripts", response = InputStreamResource.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = "microservice not found", response = String.class),
            @ApiResponse(code = 500, message = "resource grant error", response = String.class)})
    @PreAuthorize("hasRole('ATP_ADMIN')")
    public ResponseEntity<InputStreamResource> downloadContributionScripts(
            @ApiParam(value = "contribution id") @PathVariable("id") @Pattern(regexp = Constant.REG_ID) String id) {
        return contributionService.downloadContributions(id);
    }
}

