/*
 * Copyright 2020-2021 Huawei Technologies Co., Ltd.
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

package org.edgegallery.atp.utils;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.constant.ExceptionConstant;
import org.edgegallery.atp.interfaces.filter.AccessTokenFilter;
import org.edgegallery.atp.model.task.testscenarios.TaskTestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

public class CommonUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonUtil.class);

    private static RestTemplate restTemplate = new RestTemplate();

    private CommonUtil() {

    }

    /**
     * get time according to special format.
     * 
     * @return time
     */
    public static String getFormatDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(new Date());
    }

    /**
     * generate uuid randomly.
     * 
     * @return uuid
     */
    public static String generateId() {
        return UUID.randomUUID().toString();
    }

    /**
     * delete temp file according to fileId and file.
     * 
     * @param fileId taskId
     * @param file csar file
     */
    public static boolean deleteTempFile(String fileId, MultipartFile file) {
        return new File(new StringBuilder().append(Constant.WORK_TEMP_DIR).append(File.separator).append(fileId)
                .append(Constant.UNDER_LINE).append(file.getOriginalFilename()).toString()).delete();
    }

    /**
     * delete file according to filepath.
     * 
     * @param filePath file path
     */
    public static void deleteFile(String filePath) {
        if (!new File(filePath).delete()) {
            LOGGER.error("delete file failed.");
        }
    }

    /**
     * delete file.
     * 
     * @param file file
     */
    public static void deleteFile(File file) {
        if (!file.delete()) {
            LOGGER.error("delete file failed.");
        }
    }

    /**
     * get dependency app info from appstore.
     * 
     * @param appId appId
     * @param packageId packageId
     * @return JsonObject
     */
    public static JsonObject getAppInfoFromAppStore(String appId, String packageId) {
        HttpHeaders headers = new HttpHeaders();

        headers.set(Constant.ACCESS_TOKEN, AccessTokenFilter.context.get().get(Constant.ACCESS_TOKEN));
        HttpEntity<String> request = new HttpEntity<>(headers);

        String url = String.format(Constant.APP_STORE_GET_APP_PACKAGE, appId, packageId);
        try {
            ResponseEntity<String> response = restTemplate.exchange(Constant.PROTOCOL_APPSTORE.concat(url),
                    HttpMethod.GET, request, String.class);
            if (!HttpStatus.OK.equals(response.getStatusCode())) {
                LOGGER.error("get app info from appstore reponse failed. The status code is {}",
                        response.getStatusCode());
                return null;
            }

            return new JsonParser().parse(response.getBody()).getAsJsonObject();
        } catch (RestClientException e) {
            LOGGER.error("Failed to get app info from appstore which appId is {} exception {}", appId, e.getMessage());
        }

        return null;
    }

    /**
     * call download csar file interface from appstore.
     * 
     * @param appId appId
     * @param packageId packageId
     * @return response body
     */
    public static InputStream downloadAppFromAppStore(String appId, String packageId, Map<String, String> context) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(Constant.ACCESS_TOKEN, context.get(Constant.ACCESS_TOKEN));
        HttpEntity<String> request = new HttpEntity<>(headers);

        String url = String.format(Constant.APP_STORE_DOWNLOAD_CSAR, appId, packageId);
        LOGGER.info("downloadAppFromAppStore url: {}", url);
        try {
            ResponseEntity<Resource> response = restTemplate.exchange(Constant.PROTOCOL_APPSTORE.concat(url),
                    HttpMethod.GET, request, Resource.class);
            Resource responseBody = response.getBody();

            if (!HttpStatus.OK.equals(response.getStatusCode()) || responseBody == null) {
                LOGGER.error("download csar file from appstore reponse failed. The status code is {}",
                        response.getStatusCode());
                return null;
            }

            return responseBody.getInputStream();
        } catch (RestClientException e) {
            LOGGER.error("Failed to get app info from appstore which appId is {} exception {}", appId, e.getMessage());
        } catch (IOException e) {
            LOGGER.error("responseBody.getInputStream() failed,exception is:{}", e.getMessage());
        }

        return null;
    }


    /**
     * dependency check.
     * 
     * @param filePath csar file path
     * @param dependencyStack stack contains all dependency app.
     */
    public static void dependencyCheckSchdule(String filePath, Stack<Map<String, String>> dependencyStack,
            Map<String, String> context) {
        List<Map<String, String>> dependencyList = FileChecker.dependencyCheck(filePath);
        if (CollectionUtils.isEmpty(dependencyList)) {
            LOGGER.warn("dependencyCheckSchdule dependencyList is empty.");
            return;
        }
        dependencyStack.addAll(dependencyList);

        dependencyList.forEach(map -> {
            InputStream inputStream =
                    downloadAppFromAppStore(map.get(Constant.APP_ID), map.get(Constant.PACKAGE_ID), context);
            // analysis response and get csar file, get csar file path
            String dependencyFilePath = new StringBuilder().append(FileChecker.getDir()).append(File.separator)
                    .append("temp").append(File.separator).append(map.get(Constant.APP_ID)).append(Constant.UNDER_LINE)
                    .append(map.get(Constant.PACKAGE_ID)).toString();
            File file = new File(dependencyFilePath);
            try {
                FileUtils.copyInputStreamToFile(inputStream, file);
                dependencyCheckSchdule(dependencyFilePath, dependencyStack, context);
            } catch (IOException e) {
                String msg = "copy input stream to file failed.";
                LOGGER.error(msg);
                throw new IllegalArgumentException(msg);
            }
            if (!file.delete()) {
                LOGGER.error("dependencyCheckSchdule file {} delete failed.", file.getName());
            }
        });
    }

    /**
     * delete app instance from appo.
     * 
     * @param appInstanceId appInstanceId
     * @param context context info
     * @return response success or not.
     */
    public static boolean deleteAppInstance(String appInstanceId, Map<String, String> context) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(Constant.ACCESS_TOKEN, context.get(Constant.ACCESS_TOKEN));
        HttpEntity<String> request = new HttpEntity<>(headers);

        String url = Constant.PROTOCAL_APPO.concat(String.format(Constant.APPO_DELETE_APPLICATION_INSTANCE,
                context.get(Constant.TENANT_ID), appInstanceId));
        LOGGER.warn("deleteAppInstance URL: {}", url);
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, request, String.class);
            if (HttpStatus.OK.equals(response.getStatusCode())
                    || HttpStatus.ACCEPTED.equals(response.getStatusCode())) {
                return true;
            }
            LOGGER.error("delete app instance from appo reponse failed. The status code is {}",
                    response.getStatusCode());
        } catch (RestClientException e) {
            LOGGER.error("delete app instance from appo failed, appInstanceId is {} exception {}", appInstanceId,
                    e.getMessage());
        }

        return false;
    }

    /**
     * get package info from csar file.
     * 
     * @param filePath file path
     * @return package info
     */
    public static Map<String, String> getPackageInfo(String filePath) {
        Map<String, String> packageInfo = new HashMap<String, String>();
        try (ZipFile zipFile = new ZipFile(filePath)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.getName().split(Constant.SLASH).length == 1 && fileSuffixValidate("mf", entry.getName())) {
                    try (BufferedReader br = new BufferedReader(
                            new InputStreamReader(zipFile.getInputStream(entry), StandardCharsets.UTF_8))) {
                        String line = "";
                        while ((line = br.readLine()) != null) {
                            // prefix: path
                            if (line.trim().startsWith(Constant.APP_NAME)) {
                                packageInfo.put(Constant.APP_NAME, line.split(Constant.COLON)[1].trim());
                            }
                            if (line.trim().startsWith(Constant.APP_VERSION)) {
                                packageInfo.put(Constant.APP_VERSION, line.split(Constant.COLON)[1].trim());
                            }
                            if (line.trim().startsWith(Constant.PROVIDER_ID)) {
                                packageInfo.put(Constant.PROVIDER_ID, line.split(Constant.COLON)[1].trim());
                            }
                        }
                    }
                }

                if (entry.getName().split(Constant.SLASH).length == 2 && "SwImageDesc.json"
                        .equals(entry.getName().substring(entry.getName().lastIndexOf(Constant.SLASH) + 1))) {
                    try (BufferedReader br = new BufferedReader(
                            new InputStreamReader(zipFile.getInputStream(entry), StandardCharsets.UTF_8))) {
                        String line = "";
                        while ((line = br.readLine()) != null) {
                            // prefix: path
                            if (line.trim().startsWith("\"architecture\"")) {
                                String architecture = line.split(Constant.COLON)[1].trim();
                                architecture = architecture.replaceAll("[\",]", "");
                                packageInfo.put(Constant.ARCHITECTURE, architecture);
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
     * uuid validate.
     * 
     * @param param parameter
     */
    public static void isUuidPattern(String param) {
        Pattern pattern = Pattern.compile("[0-9a-f]{8}(-[0-9a-f]{4}){3}-[0-9a-f]{12}");
        if (!pattern.matcher(param).matches()) {
            LOGGER.error("param is not uuid pattern.");
            throw new IllegalArgumentException(String.format("%s is not uuid pattern.", param));
        }
    }

    /**
     * validate context is not empty.
     */
    public static void validateContext() {
        if (null == AccessTokenFilter.context.get()) {
            LOGGER.error("context is null.");
            throw new IllegalArgumentException(ExceptionConstant.CONTEXT_IS_NULL);
        }
    }

    /**
     * set test case result according to response.
     * 
     * @param response execute response result
     * @param taskTestCase taskTestCase
     */
    public static void setResult(Object response, TaskTestCase taskTestCase) {
        if (null == response) {
            LOGGER.error(ExceptionConstant.METHOD_RETURN_IS_NULL);
            taskTestCase.setResult(Constant.FAILED);
            taskTestCase.setReason(ExceptionConstant.METHOD_RETURN_IS_NULL);
        } else if (Constant.SUCCESS.equalsIgnoreCase(response.toString())) {
            taskTestCase.setResult(Constant.SUCCESS);
        } else {
            taskTestCase.setResult(Constant.FAILED);
            taskTestCase.setReason(response.toString());
        }
    }

    /**
     * validate fileName is .pattern
     * 
     * @param pattern filePattern
     * @param fileName fileName
     * @return
     */
    public static boolean fileSuffixValidate(String pattern, String fileName) {
        String suffix = fileName.substring(fileName.lastIndexOf(Constant.DOT) + 1, fileName.length());
        return StringUtils.isNotBlank(suffix) && suffix.equals(pattern);
    }

    /**
     * param is not empty.
     * 
     * @param param param
     * @param msg error msg
     */
    public static void paramIsEmpty(Object param, String msg) {
        if (null == param) {
            LOGGER.error(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * get java class name.
     * 
     * @param file java file
     * @return class name
     */
    public static String getClassPath(File file) {
        String className = Constant.EMPTY;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file.getCanonicalPath()), StandardCharsets.UTF_8))) {
            String line = "";
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("public class")) {
                    String[] arr = line.split("\\s+");
                    className = arr[2];
                    LOGGER.info("className: {}", className);
                    break;
                }
            }

            return className;
        } catch (IOException e) {
            LOGGER.error("get class path failed.");
            throw new IllegalArgumentException("get class path failed.");
        }
    }

    /**
     * set fail response body.
     * 
     * @param id id
     * @param nameEn nameEn
     * @param type testScenario or testCase or testSuite
     * @param errCode errCode
     * @param errMsg errMsg
     * @param params params
     * @return fail response body
     */
    public static JSONObject setFailureRes(String id, String nameEn, String type, int errCode, String errMsg,
            List<String> params) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.ID, id);
        jsonObject.put(Constant.NAME_EN, nameEn);
        jsonObject.put(Constant.TYPE, type);
        jsonObject.put(Constant.ERROR_CODE, errCode);
        jsonObject.put(Constant.ERROR_MSG, errMsg);
        jsonObject.put(Constant.PARAMS, params);
        return jsonObject;
    }

    /**
     * set param value, if param equal null, return defaultValue.
     * 
     * @param param param
     * @param defaultValue defaultValue
     * @return param
     */
    public static String setParamOrDefault(String param, String defaultValue) {
        return null == param ? defaultValue : param;
    }

    /**
     * param length not bigger than length.
     * 
     * @param param param
     * @param length standard length
     * @return param length not bigger than length
     */
    public static boolean isLengthOk(String param, int length) {
        if (param.length() > length) {
            LOGGER.error("param {} size can not longer than {}", param, length);
            return false;
        }
        return true;
    }
}
