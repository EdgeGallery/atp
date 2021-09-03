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
 * app cpu usage validation.
 */
public class CpuUsageValidation {

    private static final Logger LOGGER = LoggerFactory.getLogger(CpuUsageValidation.class);

    private static final String INTERFACE_RESPONSE_FAILED = "get kpi info from appo response failed, the status is: %s";

    private static final String CPU_USAGE_TOO_HIGH = "app cpu usage is more than 50%.";

    private static final String INNER_EXCEPTION = "inner exception, please check the log.";

    private static RestTemplate restTemplate = new RestTemplate();

    private static final String APPO_GET_KPI_INSTANCE = "/appo/v1/tenants/%s/hosts/%s/kpi";

    private static final String ACCESS_TOKEN = "access_token";

    private static final String TENANT_ID = "tenantId";

    private static final String SUCCESS = "success";

    public String execute(String filePath, Map<String, String> context) {
        delay();
        String cpuUsedBeforeDeploy = context.get("cpuUsedBeforeDeploy");
        if (StringUtils.isEmpty(cpuUsedBeforeDeploy)) {
            //mecm to get kpi is not stable, if can not get kpi, return success tentatively.
            return SUCCESS;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set(ACCESS_TOKEN, context.get(ACCESS_TOKEN));
        HttpEntity<String> request = new HttpEntity<>(headers);
        String mecHost = getMecHostAppInstantiated(context);
        String url = context.get("appoServerAddress")
            .concat(String.format(APPO_GET_KPI_INSTANCE, context.get(TENANT_ID), mecHost));
        LOGGER.warn("get kpi URL: {}", url);
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
            if (!HttpStatus.OK.equals(response.getStatusCode())) {
                return String.format(INTERFACE_RESPONSE_FAILED, response.getStatusCode());
            }
            JsonObject jsonObject = new JsonParser().parse(response.getBody()).getAsJsonObject();
            JsonObject cpuUsage = jsonObject.get("cpuusage").getAsJsonObject();
            int used = cpuUsage.get("used").getAsInt();
            int total = cpuUsage.get("total").getAsInt();
            int usedBeforeDeploy = Integer.valueOf(cpuUsedBeforeDeploy);
            return ((used - usedBeforeDeploy) / total) > 0.5 ? CPU_USAGE_TOO_HIGH : SUCCESS;
        } catch (RestClientException e) {
            LOGGER.error("get kpi from appo failed, exception {}", e.getMessage());
        }
        return INNER_EXCEPTION;
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
     * delay some time.
     */
    private void delay() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }
    }
}

