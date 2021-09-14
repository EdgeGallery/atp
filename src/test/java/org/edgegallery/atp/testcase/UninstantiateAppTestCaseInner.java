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
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * terminate app instance.
 */
public class UninstantiateAppTestCaseInner {

    private static final Logger LOGGER = LoggerFactory.getLogger(UninstantiateAppTestCaseInner.class);

    private static final String UNINSTANTIATE_APP_FAILED
        = "delete instantiate app from appo failed, the appInstanceId is: ";

    private static final String DELETE_EDGE_PKG_FAILED = "delete edge package from apm failed.";

    private static final String DELETE_APM_PKG_FAILED = "delete apm package from apm failed.";

    private static RestTemplate restTemplate = new RestTemplate();

    private static final String APPO_DELETE_APPLICATION_INSTANCE = "/appo/v1/tenants/%s/app_instances/%s";

    private static final String APM_DELETE_EDGE_PACKAGE = "/apm/v1/tenants/%s/packages/%s/hosts/%s";

    private static final String APM_DELETE_APM_PACKAGE = "/apm/v1/tenants/%s/packages/%s";

    private static final String ACCESS_TOKEN = "access_token";

    private static final String TENANT_ID = "tenantId";

    private static final String APP_INSTANCE_ID = "appInstanceId";

    private static final String PACKAGE_ID = "packageId";

    private static final String SUCCESS = "success";

    /**
     * execute test case.
     *
     * @param filePath csar file path
     * @param context context info
     * @return execute result
     */
    public String execute(String filePath, Map<String, String> context) {
        delay();
        String appInstanceId = context.get(APP_INSTANCE_ID);
        if (null == appInstanceId) {
            LOGGER.info("appInstanceId is null, return success.");
            return SUCCESS;
        }
        String hostIp = getMecHostAppInstantiated(context);

        if (!deleteAppInstance(appInstanceId, context)) {
            return UNINSTANTIATE_APP_FAILED;
        }
        if (!deleteEdgePackage(context, hostIp)) {
            return DELETE_EDGE_PKG_FAILED;
        }
        if (!deleteApmPackage(context)) {
            return DELETE_APM_PKG_FAILED;
        }
        return SUCCESS;
    }

    /**
     * delete edge package.
     *
     * @param context context
     * @param hostIp hostIp
     * @return delete successfully
     */
    private boolean deleteEdgePackage(Map<String, String> context, String hostIp) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(ACCESS_TOKEN, context.get(ACCESS_TOKEN));
        HttpEntity<String> request = new HttpEntity<>(headers);

        String url = context.get("apmServerAddress")
            .concat(String.format(APM_DELETE_EDGE_PACKAGE, context.get(TENANT_ID), context.get(PACKAGE_ID), hostIp));
        LOGGER.warn("deleteEdgePkg URL: {}", url);
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, request, String.class);
            if (HttpStatus.OK.equals(response.getStatusCode())) {
                return true;
            }
            LOGGER.error("deleteEdgePkg reponse failed. The status code is {}", response.getStatusCode());
        } catch (RestClientException e) {
            LOGGER.error("deleteEdgePkg failed, exception {}", e.getMessage());
        }

        return false;
    }

    /**
     * delete apm package.
     *
     * @param context context
     * @return delete successfully
     */
    private boolean deleteApmPackage(Map<String, String> context) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(ACCESS_TOKEN, context.get(ACCESS_TOKEN));
        HttpEntity<String> request = new HttpEntity<>(headers);

        String url = context.get("apmServerAddress")
            .concat(String.format(APM_DELETE_APM_PACKAGE, context.get(TENANT_ID), context.get(PACKAGE_ID)));
        LOGGER.warn("deleteApmPkg URL: {}", url);
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, request, String.class);
            if (HttpStatus.OK.equals(response.getStatusCode())) {
                return true;
            }
            LOGGER.error("deleteApmPkg reponse failed. The status code is {}", response.getStatusCode());
        } catch (RestClientException e) {
            LOGGER.error("deleteApmPkg failed, aexception {}", e.getMessage());
        }

        return false;
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
     * delete app instance from appo
     *
     * @param appInstanceId appInstanceId
     * @param context context info
     * @return response success or not.
     */
    private boolean deleteAppInstance(String appInstanceId, Map<String, String> context) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(ACCESS_TOKEN, context.get(ACCESS_TOKEN));
        HttpEntity<String> request = new HttpEntity<>(headers);

        String url = context.get("appoServerAddress")
            .concat(String.format(APPO_DELETE_APPLICATION_INSTANCE, context.get(TENANT_ID), appInstanceId));
        LOGGER.warn("deleteAppInstance URL: {}", url);
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, request, String.class);
            if (HttpStatus.OK.equals(response.getStatusCode()) || HttpStatus.ACCEPTED
                .equals(response.getStatusCode())) {
                return true;
            }
            LOGGER
                .error("delete app instance from appo reponse failed. The status code is {}", response.getStatusCode());
        } catch (RestClientException e) {
            LOGGER.error("delete app instance from appo failed, appInstanceId is {} exception {}", appInstanceId,
                e.getMessage());
        }

        return false;
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
