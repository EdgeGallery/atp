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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
 * validate secondary register service to mep.
 */
public class SecondaryRegisterService2Mep {
    private static final Logger LOGGER = LoggerFactory.getLogger(SecondaryRegisterService2Mep.class);

    private static final String SUCCESS = "success";

    private static final String GET_MEP_TOKEN_FAILED = "get token from mep failed.";

    private static final String REGISTER_SERVICE_FAILED = "register service to mep failed.";

    private static final String INNER_EXCEPTION = "inner exception, please check the log.";

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
        protocol = context.get("protocol");
        if (StringUtils.isEmpty(ip)) {
            LOGGER.error(MEP_HOST_IP_IS_NULL);
            //ignore
            return SUCCESS;
        }
        String hostIp = ip.concat(":30443");

        String token = context.get("authoration");
        if (null == token) {
            return GET_MEP_TOKEN_FAILED;
        }
        //construct first call failed scenario
        LOGGER.info("construct first call failed scenario.");
        if (!registerService("", hostIp, context)) {
            LOGGER.info("second call.");
            return registerService(token, hostIp, context) ? SUCCESS : REGISTER_SERVICE_FAILED;
        }
        return INNER_EXCEPTION;
    }

    /**
     * register service.
     *
     * @param token token
     * @param hostIp hostIp
     * @return call successful
     */
    private boolean registerService(String token, String hostIp, Map<String, String> context) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer ".concat(token));
        String body = mockMepRegisterReq();
        HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);
        String url = protocol.concat(hostIp)
            .concat("/mep/mec_service_mgmt/v1/applications/5abe4782-2c70-4e47-9a4e-0ee3a1a0fd1f/services");
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
            if (HttpStatus.CREATED.equals(response.getStatusCode())) {
                JsonObject jsonObject = new JsonParser().parse(response.getBody()).getAsJsonObject();
                context.put("serInstanceId2th", jsonObject.get("serInstanceId").getAsString());
                return true;
            }
            LOGGER.info("register service failed status: {}", response.getStatusCode());
        } catch (RestClientException e) {
            LOGGER.error("register service failed,exception");
        }
        return false;
    }

    /**
     * construct register service req.
     *
     * @return request body
     */
    private String mockMepRegisterReq() {
        String req = "{\n" + "  \"serName\": \"testService\",\n" + "  \"serCategory\": {\n"
            + "    \"href\": \"/what/is/href\",\n" + "\t\"id\": \"id9998\",\n" + "\t\"name\": \"test_service\",\n"
            + "\t\"version\": \"1.0.1\"\n" + "  },\n" + "  \"version\": \"1.0.0\",\n" + "  \"state\": \"ACTIVE\",\n"
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
