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
 * validate update registered service to mep.
 */
public class UpdateRegisteredService2Mep {
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateRegisteredService2Mep.class);

    private static final String SUCCESS = "success";

    private static final String GET_MEP_TOKEN_FAILED = "get token from mep failed.";

    private static final String UPDATE_REGISTERED_SERVICE_FAILED = "update registered service to mep failed.";

    private static final String MEP_HOST_IP_IS_NULL = "mep host ip is empty.";

    private static String protocol;

    private static RestTemplate restTemplate = new RestTemplate();

    /**
     * execute test case.
     *
     * @param filePath csar file path
     * @param context context info
     * @return execute result
     */
    public String execute(String filePath, Map<String, String> context) {
        String ip = context.get("mepHostIp");
        if (StringUtils.isEmpty(ip)) {
            LOGGER.error(MEP_HOST_IP_IS_NULL);
            //ignore
            return SUCCESS;
        }
        String hostIp = ip.concat(":30443");
        protocol = context.get("protocol");
        String token = context.get("authoration");
        if (null == token) {
            return GET_MEP_TOKEN_FAILED;
        }

        return updateRegisteredService(token, hostIp, context) ? SUCCESS : UPDATE_REGISTERED_SERVICE_FAILED;
    }

    /**
     * register service.
     *
     * @param token token
     * @param hostIp hostIp
     * @return call successful
     */
    private boolean updateRegisteredService(String token, String hostIp, Map<String, String> context) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer ".concat(token));
        headers.set("X-AppinstanceID", context.get("mepInstanceId"));
        String body = mockMepUpdateReq();
        HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);
        String url = protocol.concat(hostIp).concat("/mep/mec_service_mgmt/v1/applications/")
            .concat(context.get("mepInstanceId")).concat("/services/".concat(context.get("serInstanceId")));
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, String.class);
            if (HttpStatus.OK.equals(response.getStatusCode())) {
                return true;
            }
            LOGGER.info("update registered service failed status: {}", response.getStatusCode());
        } catch (RestClientException e) {
            LOGGER.error("update registered service failed,exception {}", e);
        }
        return false;
    }

    /**
     * construct register service req.
     *
     * @return request body
     */
    private String mockMepUpdateReq() {
        String req = "{\n" + "  \"serName\": \"testService\",\n" + "  \"serCategory\": {\n"
            + "    \"href\": \"/what/is/href\",\n" + "\t\"id\": \"id9998\",\n" + "\t\"name\": \"test_service\",\n"
            + "\t\"version\": \"1.0.3\"\n" + "  },\n" + "  \"version\": \"1.0.2\",\n" + "  \"state\": \"ACTIVE\",\n"
            + "  \"transportId\": \"Rest1\",\n" + "\t\"transportInfo\": {\n"
            + "\t\t\"id\": \"dc96e9d5-6dd3-4d0e-8a24-462956cd1a7f\",\n"
            + "\t\t\"name\": \"dc96e9d5-6dd3-4d0e-8a24-462956cd1a7f\",\n"
            + "\t\t\"description\": \"it is transportInfo\",\n" + "\t\t\"type\": \"REST_HTTP\",\n"
            + "\t\t\"protocol\": \"HTTP\",\n" + "\t\t\"version\": \"1.1\",\n" + "\t\t\"endpoint\": {\n"
            + "\t\t\t\"uris\": [\n"
            + "\t\t\t\t\"http://abc.com/mep-adapter/v1/service/5d8783f9-b050-4ad1-b02d-dfeec05c58ba\"\n" + "\t\t\t],\n"
            + "\t\t\t\"addresses\": [],\n" + "\t\t\t\"alternative\": null\n" + "\t\t},\n" + "\t\t\"security\": {\n"
            + "\t\t\t\"oAuth2Info\": {\n" + "\t\t\t\t\"grantTypes\": [\n" + "\t\t\t\t\t\"OAUTH2_CLIENT_CREDENTIALS\"\n"
            + "\t\t\t\t],\n" + "\t\t\t\t\"tokenEndpoint\": \"http://apigw.mep.com/token\"\n" + "\t\t\t}\n" + "\t\t}\n"
            + "\t},\n" + "  \"serializer\": \"JSON\",\n" + "  \"scopeOfLocality\": \"MEC_SYSTEM\",\n"
            + "  \"consumedLocalOnly\": false,\n" + "  \"livenessInterval\": 60,\n" + "  \"isLocal\": true\n" + "}";
        return req;
    }
}
