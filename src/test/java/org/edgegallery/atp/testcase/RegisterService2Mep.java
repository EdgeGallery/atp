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
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * validate register service to mep.
 */
public class RegisterService2Mep {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterService2Mep.class);

    private static final String TIME = "20210909T171702Z";

    private static final String NEXT_LINE = "\n";

    private static final String SECRETE_KEY = "DXPb4sqElKhcHe07Kw5uorayETwId1JOjjOIRomRs5wyszoCR5R7AtVa28KT3lSc";

    private static final String SUCCESS = "success";

    private static final String GET_MEP_TOKEN_FAILED = "get token from mep failed.";

    private static final String REGISTER_SERVICE_FAILED = "register service to mep failed.";

    private static RestTemplate restTemplate = new RestTemplate();

    /**
     * execute test case.
     *
     * @param filePath csar file path
     * @param context context info
     * @return execute result
     */
    public String execute(String filePath, Map<String, String> context) {
        String hostIp = getMecHostAppInstantiated(context).concat(":30443");
        String token = getMepToken(hostIp);
        if (null == token) {
            return GET_MEP_TOKEN_FAILED;
        }
        return registerService(token, hostIp) ? SUCCESS : REGISTER_SERVICE_FAILED;
    }

    /**
     * register service.
     *
     * @param token token
     * @param hostIp hostIp
     * @return call successful
     */
    private boolean registerService(String token, String hostIp) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer ".concat(token));
        String body = mockMepRegisterReq();
        HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);
        String url = "https://".concat(hostIp)
            .concat("/mep/mec_service_mgmt/v1/applications/5abe4782-2c70-4e47-9a4e-0ee3a1a0fd1f/services");
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
            if (HttpStatus.CREATED.equals(response.getStatusCode())) {
                return true;
            }
            LOGGER.info("register service failed status: {}", response.getStatusCode());
        } catch (RestClientException e) {
            LOGGER.error("register service failed,exception {}", e);
        }
        return false;
    }

    /**
     * send request for getting token from mep.
     *
     * @param hostIp hostIp
     * @return token
     */
    private String getMepToken(String hostIp) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", getSignValue(hostIp));
        headers.set("x-sdk-date", TIME);
        headers.set("Host", hostIp);
        HttpEntity<String> request = new HttpEntity<>(headers);

        String url = "https://".concat(hostIp).concat("/mep/token");
        LOGGER.warn("get mep token URL: {}", url);
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
            if (HttpStatus.OK.equals(response.getStatusCode())) {
                JsonObject jsonObject = new JsonParser().parse(response.getBody()).getAsJsonObject();
                return null == jsonObject.get("access_token") ? null : jsonObject.get("access_token").getAsString();
            }
        } catch (RestClientException e) {
            LOGGER.error("get mep token failed,exception {}", e);
        }
        return null;
    }

    /**
     * get ak/sk sign value.
     *
     * @param hostIp hostIp
     * @return sign value
     */
    private String getSignValue(String hostIp) {
        try {
            String time = TIME;
            //contruct response
            StringBuffer request = new StringBuffer();
            request.append("POST").append(NEXT_LINE).append("/mep/token/").append(NEXT_LINE).append("")
                .append(NEXT_LINE).append("content-type:application/json").append(NEXT_LINE).append("host:")
                .append(hostIp).append(NEXT_LINE).append("x-sdk-date:").append(time).append(NEXT_LINE).append(NEXT_LINE)
                .append("content-type;host;x-sdk-date").append(NEXT_LINE);

            //HexEncode(Hash(RequestPayload))，
            request.append(getHashValue(""));

            //contruct signData
            StringBuffer signData = new StringBuffer();
            signData.append("SDK-HMAC-SHA256").append(NEXT_LINE).append(time).append(NEXT_LINE)
                .append(getHashValue(new String(request)));

            //sign data
            SecretKeySpec secretKeySpec = new SecretKeySpec(SECRETE_KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            String algothim = secretKeySpec.getAlgorithm();
            System.out.println(algothim);
            Mac mac = Mac.getInstance(secretKeySpec.getAlgorithm());
            mac.init(secretKeySpec);
            byte[] array = mac.doFinal(signData.toString().getBytes(StandardCharsets.UTF_8));
            StringBuffer signValue = new StringBuffer();
            for (byte b : array) {
                signValue.append(String.format("%02x", b));
            }

            //contruct authorization
            StringBuffer auth = new StringBuffer();
            auth.append(
                "SDK-HMAC-SHA256 Access=QVUJMSUMgS0VZLS0tLS0, SignedHeaders=content-type;host;x-sdk-date, Signature=")
                .append(signValue.toString());
            System.out.println(auth);
            return auth.toString();
        } catch (Exception e) {
            LOGGER.error("get sign value failed. {}", e);
            return null;
        }
    }

    /**
     * get hash256 value and return 16 hex value.
     *
     * @param data src data
     * @return hash value
     * @throws Exception Exception
     */
    private String getHashValue(String data) throws Exception {
        StringBuilder signBody = new StringBuilder();
        MessageDigest object = MessageDigest.getInstance("SHA-256");
        byte[] encrypted = object.digest(data.getBytes("UTF-8"));
        for (byte b : encrypted) {
            signBody.append(String.format("%02x", b));
        }
        return signBody.toString();
    }

    /**
     * get app instantiate ip from context.
     *
     * @param context context info
     * @return instantiate mec host
     */
    private String getMecHostAppInstantiated(Map<String, String> context) {
        String mecHostIpList = context.get("mecHostIpList");
        if (null == mecHostIpList) {
            return null;
        }
        String[] hostArray = mecHostIpList.split(",");
        return hostArray[0];
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
