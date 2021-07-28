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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.constant.ErrorCode;
import org.edgegallery.atp.model.PageResult;
import org.edgegallery.atp.model.ResponseObject;
import org.edgegallery.atp.model.config.Config;
import org.edgegallery.atp.model.config.ConfigBase;
import org.edgegallery.atp.service.ConfigService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RestSchema(schemaId = "config")
@RequestMapping("/edgegallery/atp/v1")
@Api(tags = {"ATP Config Controller"})
@Validated
public class ConfigController {

    @Autowired
    ConfigService configService;

    /**
     * create a config interface.
     *
     * @param config config info
     * @return config info
     */
    @PostMapping(value = "/configs", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "create a config.", response = String.class)
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "microservice not found", response = String.class),
        @ApiResponse(code = 500, message = "resource grant error", response = String.class)
    })
    @PreAuthorize("hasRole('ATP_ADMIN')")
    public ResponseEntity<ResponseObject<Config>> createConfig(
        @ApiParam(value = "config request param") @RequestBody ConfigBase config) {
        ResponseObject<Config> result = new ResponseObject<Config>(configService.createConfig(config),
            ErrorCode.RET_CODE_SUCCESS, null, "create a config successfully.");
        return ResponseEntity.ok(result);
    }

    /**
     * update a config.
     *
     * @param config config info
     * @param id config id
     * @return config info after updated
     * @throws FileNotExistsException FileNotExistsException
     */
    @PutMapping(value = "/configs/{id}", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "update a config.", response = String.class)
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "microservice not found", response = String.class),
        @ApiResponse(code = 500, message = "resource grant error", response = String.class)
    })
    @PreAuthorize("hasRole('ATP_ADMIN')")
    public ResponseEntity<ResponseObject<Config>> updateConfig(
        @ApiParam(value = "config request param") @RequestBody ConfigBase config,
        @ApiParam(value = "config id") @PathVariable("id") @Pattern(regexp = Constant.REG_ID) String id)
        throws FileNotExistsException {
        ResponseObject<Config> result = new ResponseObject<Config>(configService.updateConfig(config, id),
            ErrorCode.RET_CODE_SUCCESS, null, "update a config successfully.");
        return ResponseEntity.ok(result);
    }

    /**
     * delete a config by id.
     *
     * @param id config id
     * @return true
     */
    @DeleteMapping(value = "/configs/{id}", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "delete a config.", response = Boolean.class)
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "microservice not found", response = String.class),
        @ApiResponse(code = 500, message = "resource grant " + "error", response = String.class)
    })
    @PreAuthorize("hasRole('ATP_ADMIN')")
    public ResponseEntity<Boolean> deleteConfig(
        @ApiParam(value = "config id") @PathVariable("id") @Pattern(regexp = Constant.REG_ID) String id) {
        return ResponseEntity.ok(configService.deleteConfig(id));
    }

    /**
     * query a config by id.
     *
     * @param id config id
     * @return config info
     * @throws FileNotFoundException FileNotFoundException
     */
    @GetMapping(value = "/configs/{id}", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "get one config.", response = Config.class)
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "microservice not found", response = String.class),
        @ApiResponse(code = 500, message = "resource grant " + "error", response = String.class)
    })
    @PreAuthorize("hasRole('ATP_GUEST') || hasRole('ATP_TENANT') || hasRole('ATP_ADMIN')")
    public ResponseEntity<ResponseObject<Config>> queryConfig(
        @ApiParam(value = "config id") @PathVariable("id") @Pattern(regexp = Constant.REG_ID) String id)
        throws FileNotFoundException {
        ResponseObject<Config> result = new ResponseObject<Config>(configService.queryConfig(id),
            ErrorCode.RET_CODE_SUCCESS, null, "query a config by id successfully.");
        return ResponseEntity.ok(result);
    }

    /**
     * query all configs.
     *
     * @param limit limit
     * @param offset offset
     * @param locale locale
     * @param name name
     * @return config list with pagination
     */
    @GetMapping(value = "/configs", produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "get all configs.", response = PageResult.class)
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "microservice not found", response = String.class),
        @ApiResponse(code = 500, message = "resource grant " + "error", response = String.class)
    })
    @PreAuthorize("hasRole('ATP_GUEST') || hasRole('ATP_TENANT') || hasRole('ATP_ADMIN')")
    public ResponseEntity<PageResult<Config>> queryAllConfigs(
        @ApiParam(value = "limit") @QueryParam("limit") @NotNull int limit,
        @ApiParam(value = "offset") @QueryParam("offset") @NotNull int offset,
        @ApiParam(value = "locale language") @Length(max = Constant.LENGTH_64) @QueryParam("locale") String locale,
        @ApiParam(value = "config name") @Length(max = Constant.LENGTH_64) @QueryParam("name") String name) {
        return ResponseEntity.ok(configService.queryAllConfigs(limit, offset, locale, name));
    }
}
