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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.edgegallery.atp.utils.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Instantiate app.
 *
 */
public class InstantiateAppTestCaseInner {

    private static final Logger LOGGER = LoggerFactory.getLogger(InstantiateAppTestCaseInner.class);

    private static final String RESPONSE_FROM_APM_FAILED = "upload csar file to apm failed, and the response code is: ";
    private static final String INSTANTIATE_APP_FAILED = "instantiate app from appo failed.";
    private static final String APP_NAME = "app_product_name";
    private static final String APP_VERSION = "app_package_version";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String APP_CLASS = "app_class";
    private static final String APP_INSTANCE_ID = "appInstanceId";
    private static final String APP_ID = "appId";
    private static final String PACKAGE_ID = "packageId";
    private static final String SUCCESS = "success";
    private static final String APM_UPLOAD_PACKAGE = "/apm/v1/tenants/%s/packages/upload";
    private static final String APM_GET_PACKAGE = "/apm/v1/tenants/%s/packages/%s";
    private static final String INVENTORY_GET_MECHOSTS_URL = "/inventory/v1/mechosts";
    private static final String TENANT_ID = "tenantId";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPO_CREATE_APPINSTANCE = "/appo/v1/tenants/%s/app_instances";
    private static final String APPLICATION_JSON = "application/json";
    private static final String CREATED = "Created";
    private static final String APPO_INSTANTIATE_APP = "/appo/v1/tenants/%s/app_instances/%s";
    private static final String APPO_GET_INSTANCE = "/appo/v1/tenants/%s/app_instance_infos/%s";
    private static final String PROVIDER_ID = "app_provider_id";
    private static final String INSTANTIATED = "instantiated";
    private static final String ARCHITECTURE = "app_architecture";
    private static final String VM = "vm";

    private RestTemplate restTemplate = new RestTemplate();

    public String execute(String filePath, Map<String, String> context) {
        Map<String, String> packageInfo = getPackageInfo(filePath);
        context.put(APP_CLASS, packageInfo.get(APP_CLASS));

        String hostIp = getMecHost(context, packageInfo);
        if (isEmpty(hostIp)) {
            LOGGER.error("host ip is empty.");
            return "host ip is empty";
        }

        ResponseEntity<String> response = uploadFileToAPM(filePath, context, hostIp, packageInfo);
        if (null == response || !(HttpStatus.OK.equals(response.getStatusCode())
                || HttpStatus.ACCEPTED.equals(response.getStatusCode()))) {
            LOGGER.error("uploadFileToAPM failed, response: {}", response);
            return null == response ? RESPONSE_FROM_APM_FAILED
                    : RESPONSE_FROM_APM_FAILED.concat(response.getStatusCode().toString());
        }

        JsonObject jsonObject = new JsonParser().parse(response.getBody()).getAsJsonObject();
        Map<String, String> appInfo = new HashMap<String, String>() {
            {
                put(APP_NAME, packageInfo.get(APP_NAME));
                put(APP_ID, jsonObject.get("appId").getAsString());
                put(PACKAGE_ID, jsonObject.get("appPackageId").getAsString());
            }
        };

        // get distribution status from apm
        if (!getApmPackage(context, appInfo.get(PACKAGE_ID), hostIp)) {
            return "get distribution status from apm failed.";
        }

        // instantiate original app
        String appInstanceId = createInstanceFromAppo(context, appInfo, hostIp);
        context.put(APP_INSTANCE_ID, appInstanceId);

        LOGGER.info("original appInstanceId: {}", appInstanceId);

        return null != appInstanceId ? SUCCESS : INSTANTIATE_APP_FAILED;

    }

    /**
     * get package from apm.
     * 
     * @param context context
     * @param packageId packageId
     * @param hostIp hostIp
     * @return
     */
    private boolean getApmPackage(Map<String, String> context, String packageId, String hostIp) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(ACCESS_TOKEN, context.get(ACCESS_TOKEN));
        HttpEntity<String> request = new HttpEntity<>(headers);
        String url = context.get("apmServerAddress")
                .concat(String.format(APM_GET_PACKAGE, context.get(TENANT_ID), packageId));
        LOGGER.warn("getApmPackage URL: " + url);

        long startTime = System.currentTimeMillis();
        while (true) {
            try {
                // time out limit
                if ((System.currentTimeMillis() - startTime) > 120000) {
                    LOGGER.error("get package {} from apm time out", packageId);
                    return false;
                }

                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
                if (!HttpStatus.OK.equals(response.getStatusCode())) {
                    LOGGER.error("get package from apm reponse failed. The status code is {}",
                            response.getStatusCode());
                    return false;
                }

                JsonObject jsonObject = new JsonParser().parse(response.getBody()).getAsJsonObject();
                JsonArray mecHostInfo = jsonObject.get("mecHostInfo").getAsJsonArray();
                for (JsonElement mecHost : mecHostInfo) {
                    JsonObject mecHostObject = mecHost.getAsJsonObject();
                    String status = mecHostObject.get("status").getAsString();
                    String hostIpReq = mecHostObject.get("hostIp").getAsString();
                    if (hostIp.equals(hostIpReq)) {
                        LOGGER.info("status: {}", status);
                        if ("Distributed".equalsIgnoreCase(status)) {
                            return true;
                        } else {
                            if ("Error".equalsIgnoreCase(status)) {
                                return false;
                            } else {
                                break;
                            }
                        }
                    }
                }
                Thread.sleep(6000);
            } catch (RestClientException e) {
                LOGGER.error("Failed to get package from apm which packageId is {} exception {}", packageId,
                        e.getMessage());
                return false;
            } catch (InterruptedException e) {
                LOGGER.error("thead sleep exception.");
                return false;
            }
        }
    }

    /**
     * send request to inventory to get mecHost ip.
     * 
     * @param context context info
     * @return mecHostIp
     */
    private String getMecHost(Map<String, String> context, Map<String, String> packageInfo) {
        List<String> mecHostIpList = new ArrayList<String>();
        if (isEmpty(packageInfo.get(ARCHITECTURE))) {
            LOGGER.info("not contain architecture field, default is X86");
            packageInfo.put(ARCHITECTURE, "X86");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set(ACCESS_TOKEN, context.get(ACCESS_TOKEN));
        HttpEntity<String> request = new HttpEntity<>(headers);

        String url = context.get("inventoryServerAddress").concat(INVENTORY_GET_MECHOSTS_URL);
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
                JsonElement affinity = mecHost.getAsJsonObject().get("affinity");
                JsonElement vim = mecHost.getAsJsonObject().get("vim");
                if (null != mecHostIp && null != affinity && null != vim) {
                    String vimStr = vim.getAsString();
                    vimStr = "OpenStack".equalsIgnoreCase(vimStr) ? "vm" : "container";
                    if (packageInfo.get(ARCHITECTURE).equals(affinity.getAsString())
                            && packageInfo.get(APP_CLASS).equalsIgnoreCase(vimStr)) {
                        mecHostIpList.add(mecHostIp.getAsString());
                    }
                }
            });
        } catch (RestClientException e) {
            LOGGER.error("Failed to get hosts from inventory, exception {}", e.getMessage());
            return null;
        }

        return mecHostIpList.size() == 0 ? null : mecHostIpList.get(0);
    }

    private boolean isEmpty(String str) {
        return null == str || "".equals(str);
    }

    /**
     * create app instance from appo.
     * 
     * @param filePath csar file path
     * @param context context info
     * @param appInfo contains appName,appId,appPackageId
     * @param hostIp mec host ip
     * @param ipPort protocl://ip:port
     * @return create app instance sucess or not.s
     */
    private String createInstanceFromAppo(Map<String, String> context, Map<String, String> appInfo, String hostIp) {
        Map<String, Object> body = new HashMap<>();
        body.put("appInstanceDescription", UUID.randomUUID().toString());
        body.put("appName", appInfo.get(APP_NAME));
        body.put("appPackageId", appInfo.get(PACKAGE_ID));
        body.put("appId", appInfo.get(APP_ID));
        body.put("mecHost", hostIp);

        HttpHeaders headers = new HttpHeaders();
        headers.set(ACCESS_TOKEN, context.get(ACCESS_TOKEN));
        headers.set(CONTENT_TYPE, APPLICATION_JSON);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        String url =
                context.get("appoServerAddress").concat(String.format(APPO_CREATE_APPINSTANCE, context.get(TENANT_ID)));

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
            LOGGER.info("response is: {}", response.getStatusCode());
            if (HttpStatus.OK.equals(response.getStatusCode())
                    || HttpStatus.ACCEPTED.equals(response.getStatusCode())) {
                JsonObject jsonObject = new JsonParser().parse(response.getBody()).getAsJsonObject();
                JsonObject responseBody = jsonObject.get("response").getAsJsonObject();
                if (null != responseBody) {
                    String appInstanceId = responseBody.get("app_instance_id").getAsString();
                    LOGGER.info("appInstanceId: {}", appInstanceId);
                    if (getApplicationInstance(context, appInstanceId, CREATED)
                            && instantiateAppFromAppo(context, appInstanceId)) {
                        if (getApplicationInstance(context, appInstanceId, INSTANTIATED)) {
                            return appInstanceId;
                        }
                    }
                    return null;
                }
            }
        } catch (RestClientException e) {
            LOGGER.error("Failed to create app instance from appo which appId is {} exception {}", appInfo.get(APP_ID),
                    e.getMessage());
        }
        return null;
    }

    /**
     * get application instance from appo.
     * 
     * @param context context
     * @param appInstanceId appInstanceId
     * @param status status
     * @return
     */
    private boolean getApplicationInstance(Map<String, String> context, String appInstanceId, String status) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(ACCESS_TOKEN, context.get(ACCESS_TOKEN));
        HttpEntity<String> request = new HttpEntity<>(headers);

        String url = context.get("appoServerAddress")
                .concat(String.format(APPO_GET_INSTANCE, context.get(TENANT_ID), appInstanceId));

        LOGGER.warn("getApplicationInstance URL: " + url);

        long startTime = System.currentTimeMillis();
        while (true) {
            try {
                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
                if (!HttpStatus.OK.equals(response.getStatusCode())) {
                    LOGGER.error("get application instance from appo reponse failed. The status code is {}",
                            response.getStatusCode());
                    return false;
                }

                JsonObject jsonObject = new JsonParser().parse(response.getBody()).getAsJsonObject();
                JsonObject responseBody = jsonObject.get("response").getAsJsonObject();
                LOGGER.info("status: {}, operationalStatus: {}", status,
                        responseBody.get("operationalStatus").getAsString());

                String responseStatus = responseBody.get("operationalStatus").getAsString();
                if ("Instantiation failed".equalsIgnoreCase(responseStatus)
                        || "Create failed".equalsIgnoreCase(responseStatus)) {
                    LOGGER.error("instantiate or create app failed. The status  is {}", responseStatus);
                    return false;
                }

                if (status.equalsIgnoreCase(responseStatus)) {
                    LOGGER.info("{} is {}.", appInstanceId, status);
                    break;
                }

                if ((System.currentTimeMillis() - startTime) > 40000) {
                    LOGGER.error("get instance {} from appo time out", appInstanceId);
                    return false;
                }
                Thread.sleep(5000);
            } catch (RestClientException e) {
                LOGGER.error("Failed to get application instance from appo which app_instance_id is {} exception {}",
                        appInstanceId, e.getMessage());
                return false;
            } catch (InterruptedException e) {
                LOGGER.error("thead sleep exception.");
            }
        }

        return true;
    }

    /**
     * instantiate application by appo.
     * 
     * @param context context info.
     * @param appInstanceId appInstanceId
     * @return instantiate app successful
     */
    private boolean instantiateAppFromAppo(Map<String, String> context, String appInstanceId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(ACCESS_TOKEN, context.get(ACCESS_TOKEN));
        headers.set(CONTENT_TYPE, APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request;
        if (VM.equalsIgnoreCase(context.get(APP_CLASS))) {
            Map<String, Object> body = new HashMap<String, Object>();
            // if package is vm, need parameters body
            LOGGER.info("package is vm.");
            Map<String, Object> parameters = new HashMap<String, Object>();
            setBody(parameters);
            body.put("parameters", parameters);
            request = new HttpEntity<>(body, headers);
        } else {
            request = new HttpEntity<>(headers);
        }

        String url = context.get("appoServerAddress")
                .concat(String.format(APPO_INSTANTIATE_APP, context.get(TENANT_ID), appInstanceId));
        LOGGER.info("instantiateAppFromAppo URL : {}", url);
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
            if (!HttpStatus.ACCEPTED.equals(response.getStatusCode())) {
                LOGGER.error("instantiate application from appo reponse failed. The status code is {}",
                        response.getStatusCode());
                return false;
            }
            LOGGER.info("instantiateAppFromAppo: {}", response.getStatusCode());
        } catch (RestClientException e) {
            LOGGER.error("Failed to instantiate application from appo which app_instance_id is {} exception {}",
                    appInstanceId, e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * set request parameters.
     * 
     * @param body body
     */
    private void setBody(Map<String, Object> body) {
        body.put("DC_ID", PropertiesUtil.getProperties("DC_ID"));
        body.put("az_dc", PropertiesUtil.getProperties("az_dc"));
        body.put("ak", PropertiesUtil.getProperties("ak"));
        body.put("sk", PropertiesUtil.getProperties("sk"));
        body.put("mep_certificate", PropertiesUtil.getProperties("mep_certificate"));
        body.put("app_mp1_ip", PropertiesUtil.getProperties("app_mp1_ip"));
        body.put("app_mp1_mask", PropertiesUtil.getProperties("app_mp1_mask"));
        body.put("app_mp1_gw", PropertiesUtil.getProperties("app_mp1_gw"));
        body.put("app_n6_ip", PropertiesUtil.getProperties("app_n6_ip"));
        body.put("app_n6_mask", PropertiesUtil.getProperties("app_n6_mask"));
        body.put("app_n6_gw", PropertiesUtil.getProperties("app_n6_gw"));
        body.put("app_internet_ip", PropertiesUtil.getProperties("app_internet_ip"));
        body.put("app_internet_mask", PropertiesUtil.getProperties("app_internet_mask"));
        body.put("app_internet_gw", PropertiesUtil.getProperties("app_internet_gw"));
        body.put("mep_ip", PropertiesUtil.getProperties("mep_ip"));
        body.put("mep_port", PropertiesUtil.getProperties("mep_port"));
        body.put("network_name_mep", PropertiesUtil.getProperties("network_name_mep"));
        body.put("network_mep_physnet", PropertiesUtil.getProperties("network_mep_physnet"));
        body.put("network_mep_vlanid", PropertiesUtil.getProperties("network_mep_vlanid"));
        body.put("network_name_n6", PropertiesUtil.getProperties("network_name_n6"));
        body.put("network_n6_physnet", PropertiesUtil.getProperties("network_n6_physnet"));
        body.put("network_n6_vlanid", PropertiesUtil.getProperties("network_n6_vlanid"));
        body.put("network_name_internet", PropertiesUtil.getProperties("network_name_internet"));
        body.put("network_internet_physnet", PropertiesUtil.getProperties("network_internet_physnet"));
        body.put("network_internet_vlanid", PropertiesUtil.getProperties("network_internet_vlanid"));
        body.put("ue_ip_segment", PropertiesUtil.getProperties("ue_ip_segment"));
        body.put("mec_internet_ip", PropertiesUtil.getProperties("mec_internet_ip"));
        body.put("TrafficRuleSrcAddr", PropertiesUtil.getProperties("TrafficRuleSrcAddr"));
        body.put("TrafficRuleDstAddr", PropertiesUtil.getProperties("TrafficRuleDstAddr"));
        body.put("TrafficRuleSrcPort", PropertiesUtil.getProperties("TrafficRuleSrcPort"));
        body.put("TrafficRuleDstPort", PropertiesUtil.getProperties("TrafficRuleDstPort"));
        body.put("TrafficRuleProtocol", PropertiesUtil.getProperties("TrafficRuleProtocol"));
        body.put("DnsRuleDomainName", PropertiesUtil.getProperties("DnsRuleDomainName"));
        body.put("DnsRuleIpAddressType", PropertiesUtil.getProperties("DnsRuleIpAddressType"));
        body.put("DnsRuleIpAddress", PropertiesUtil.getProperties("DnsRuleIpAddress"));
    }

    /**
     * get package info from csar file.
     * 
     * @param filePath file path
     * @return package info
     */
    private Map<String, String> getPackageInfo(String filePath) {
        Map<String, String> packageInfo = new HashMap<String, String>();
        try (ZipFile zipFile = new ZipFile(filePath)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.getName().split("/").length == 1 && fileSuffixValidate("mf", entry.getName())) {
                    try (BufferedReader br = new BufferedReader(
                            new InputStreamReader(zipFile.getInputStream(entry), StandardCharsets.UTF_8))) {
                        String line = "";
                        while ((line = br.readLine()) != null) {
                            // prefix: path
                            if (line.trim().startsWith(APP_NAME)) {
                                packageInfo.put(APP_NAME, line.split(":")[1].trim());
                            }
                            if (line.trim().startsWith(APP_VERSION)) {
                                packageInfo.put(APP_VERSION, line.split(":")[1].trim());
                            }
                            if (line.trim().startsWith(PROVIDER_ID)) {
                                packageInfo.put(PROVIDER_ID, line.split(":")[1].trim());
                            }
                            if (line.trim().startsWith(APP_CLASS)) {
                                packageInfo.put(APP_CLASS, line.split(":")[1].trim());
                            }
                        }
                    }
                }
                if (entry.getName().split("/").length == 2
                        && "SwImageDesc.json".equals(entry.getName().substring(entry.getName().lastIndexOf("/") + 1))) {
                    try (BufferedReader br = new BufferedReader(
                            new InputStreamReader(zipFile.getInputStream(entry), StandardCharsets.UTF_8))) {
                        String line = "";
                        while ((line = br.readLine()) != null) {
                            // prefix: path
                            if (line.trim().startsWith("\"architecture\"")) {
                                String architecture = line.split(":")[1].trim();
                                architecture = architecture.replaceAll("[\",]", "");
                                packageInfo.put(ARCHITECTURE, architecture);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("getPackageInfo failed. {}", e.getMessage());
        }

        return packageInfo;
    }

    /**
     * upload file to apm service.
     * 
     * @param filePath file path
     * @param context context info
     * @param ipPort protocol://ip:port
     * @param hostIp host ip
     * @return response from atp
     */
    private ResponseEntity<String> uploadFileToAPM(String filePath, Map<String, String> context, String hostIp,
            Map<String, String> packageInfo) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(filePath));
        body.add("hostList", hostIp);
        body.add("appPackageName", packageInfo.get(APP_NAME));
        body.add("appPackageVersion", packageInfo.get(APP_VERSION));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set(ACCESS_TOKEN, context.get(ACCESS_TOKEN));

        LOGGER.info("hostIp: " + hostIp);
        LOGGER.info("appPackageName: " + packageInfo.get(APP_NAME));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        String url = context.get("apmServerAddress").concat(String.format(APM_UPLOAD_PACKAGE, context.get(TENANT_ID)));
        try {
            return restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        } catch (RestClientException e) {
            LOGGER.error("Failed to upload file to apm, exception {}", e.getMessage());
        }
        return null;
    }

    /**
     * validate fileName is .pattern.
     * 
     * @param pattern filePattern
     * @param fileName fileName
     * @return
     */
    public static boolean fileSuffixValidate(String pattern, String fileName) {
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        if (null != suffix && "" != suffix && suffix.equals(pattern)) {
            return true;
        }
        return false;
    }
}


