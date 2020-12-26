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

package org.edgegallery.atp.schedule.testcase.sandbox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.constant.ExceptionConstant;
import org.edgegallery.atp.model.testcase.TestCaseResult;
import org.edgegallery.atp.schedule.testcase.TestCaseAbs;
import org.edgegallery.atp.utils.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Instantiate app.
 *
 */
public class InstantiateAppTestCase extends TestCaseAbs {

    private static final Logger LOGGER = LoggerFactory.getLogger(InstantiateAppTestCase.class);

    private RestTemplate restTemplate = new RestTemplate();

    private TestCaseResult testCaseResult = new TestCaseResult();

    @Override
    public TestCaseResult execute(String filePath, Map<String, String> context) {
        Map<String, String> packageInfo = CommonUtil.getPackageInfo(filePath);
        ResponseEntity<String> response =
                CommonUtil.uploadFileToAPM(filePath, context, getMecHost(context), packageInfo);
        if (null == response || !(HttpStatus.OK.equals(response.getStatusCode())
                || HttpStatus.ACCEPTED.equals(response.getStatusCode()))) {
            LOGGER.error("uploadFileToAPM failed, response: {}", response);
            return null == response
                    ? setTestCaseResult(Constant.FAILED, ExceptionConstant.RESPONSE_FROM_APM_FAILED, testCaseResult)
                    : setTestCaseResult(Constant.FAILED,
                            ExceptionConstant.RESPONSE_FROM_APM_FAILED.concat(response.getStatusCode().toString()),
                            testCaseResult);
        }

        JsonObject jsonObject = new JsonParser().parse(response.getBody()).getAsJsonObject();
        Map<String, String> appInfo = new HashMap<String, String>() {
            {
                put(Constant.APP_NAME, packageInfo.get(Constant.APP_NAME));
                put(Constant.APP_ID, jsonObject.get("appId").getAsString());
                put(Constant.PACKAGE_ID, jsonObject.get("appPackageId").getAsString());
            }
        };

        // instantiate original app
        String appInstanceId = CommonUtil.createInstanceFromAppo(context, appInfo, getMecHost(context));
        context.put(Constant.APP_INSTANCE_ID, appInstanceId);

        LOGGER.info("original appInstanceId: {}", appInstanceId);

        return null != appInstanceId ? setTestCaseResult(Constant.SUCCESS, Constant.EMPTY, testCaseResult)
                : setTestCaseResult(Constant.FAILED, ExceptionConstant.INSTANTIATE_APP_FAILED, testCaseResult);

    }

    /**
     * send request to inventory to get mecHost ip.
     * 
     * @param context context info
     * @return mecHostIp
     */
    private String getMecHost(Map<String, String> context) {
        List<String> mecHostIpList = new ArrayList<String>();

        HttpHeaders headers = new HttpHeaders();
        headers.set(Constant.ACCESS_TOKEN, context.get(Constant.ACCESS_TOKEN));
        HttpEntity<String> request = new HttpEntity<>(headers);

        String url = Constant.PROTOCOL_INVENTORY
                .concat(String.format(Constant.INVENTORY_GET_MECHOSTS_URL, context.get(Constant.TENANT_ID)));
        LOGGER.warn("get mechostb url: " + url);
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
            if (!HttpStatus.OK.equals(response.getStatusCode())) {
                LOGGER.error("Instantiate through applcm reponse failed. The status code is {}",
                        response.getStatusCode());
                return null;
            }

            JsonArray jsonArray = new JsonParser().parse(response.getBody()).getAsJsonArray();
            jsonArray.forEach(mecHost -> {
                JsonElement mecHostIp = mecHost.getAsJsonObject().get("mechostIp");
                if (null != mecHostIp) {
                    mecHostIpList.add(mecHostIp.getAsString());
                }
            });
        } catch (RestClientException e) {
            LOGGER.error("Failed to get hosts from inventory, exception {}", e.getMessage());
            return null;
        }

        return mecHostIpList.get(0);
    }

}
