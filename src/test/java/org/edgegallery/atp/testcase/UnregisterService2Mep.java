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

package org.edgegallery.atp.testcase;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * validate unregister service to mep.
 */
public class UnregisterService2Mep {
    private static final Logger LOGGER = LoggerFactory.getLogger(UnregisterService2Mep.class);

    private static final String SUCCESS = "success";

    private static final String UNREGISTER_SERVICE_FAILED = "unregister service to mep failed.";

    private static final String MEP_HOST_IP_IS_NULL = "mep host ip is empty.";

    private static RestTemplate restTemplate = new RestTemplate();

    /**
     * execute test case.
     *
     * @param filePath csar file path
     * @param context context info
     * @return execute result
     */
    public String execute(String filePath, Map<String, String> context) {
        if (StringUtils.isEmpty(context.get("serInstanceId"))) {
            LOGGER.warn("register service failed, and serInstanceId not exists,return success.");
            return SUCCESS;
        }
        String ip = context.get("mepHostIp");
        if (StringUtils.isEmpty(ip)) {
            LOGGER.error(MEP_HOST_IP_IS_NULL);
            return MEP_HOST_IP_IS_NULL;
        }
        String hostIp = ip.concat(":30443");
        return (unregisterService(hostIp, context, context.get("serInstanceId")) && unregisterService(hostIp, context,
            context.get("serInstanceId2th"))) ? SUCCESS : UNREGISTER_SERVICE_FAILED;
    }

    /**
     * unregister service.
     *
     * @param hostIp hostIp
     * @return unregister successful
     */
    private boolean unregisterService(String hostIp, Map<String, String> context, String serInstanceId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer ".concat(context.get("authoration")));
        headers.set("X-AppinstanceID", context.get("mepInstanceId"));
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        String url = "https://".concat(hostIp).concat("/mep/mec_service_mgmt/v1/applications/")
            .concat(context.get("mepInstanceId")).concat("/services/".concat(serInstanceId));
        try {
            ResponseEntity<String> response = restTemplate
                .exchange(url, HttpMethod.DELETE, requestEntity, String.class);
            if (HttpStatus.NO_CONTENT.equals(response.getStatusCode())) {
                context.remove("Authorization");
                return true;
            }
            LOGGER.info("unregister service failed status: {}", response.getStatusCode());
        } catch (RestClientException e) {
            LOGGER.error("unregister service failed,exception {}", e);
        }
        return false;
    }
}
