package org.edgegallery.atp.interfaces;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.ws.rs.core.MediaType;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.edgegallery.atp.constant.ErrorCode;
import org.edgegallery.atp.model.ResponseObject;
import org.edgegallery.atp.model.config.Config;
import org.edgegallery.atp.model.config.ConfigBase;
import org.edgegallery.atp.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
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
    @PreAuthorize("hasRole('ATP_TENANT') || hasRole('ATP_ADMIN')")
    public ResponseEntity<ResponseObject<Config>> createConfig(
        @ApiParam(value = "config request param") @RequestBody ConfigBase config) {
        ResponseObject<Config> result = new ResponseObject<Config>(configService.createConfig(config),
            ErrorCode.RET_CODE_SUCCESS, null, "create a config successfully.");
        return ResponseEntity.ok(result);
    }
}
