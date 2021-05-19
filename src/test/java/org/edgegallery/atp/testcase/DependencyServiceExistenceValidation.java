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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class DependencyServiceExistenceValidation {

    private static final Logger LOGGER = LoggerFactory.getLogger(DependencyServiceExistenceValidation.class);
    private static RestTemplate restTemplate = new RestTemplate();

    private static final String SUCCESS = "success";
    private static final String NODE_TEMPLATES = "node_templates";
    private static final String APP_CONFIGURATION = "app_configuration";
    private static final String PROPERTIES = "properties";
    private static final String APP_SERVICE_REQUIRED = "appServiceRequired";
    private static final String COLON = ":";
    private static final String SLASH = "/";
    private static final String STRIKE = "-";
    private static final String UNDER_LINE = "_";
    private static final String APP_ID = "appId";
    private static final String PACKAGE_ID = "packageId";
    private static final String APP_NAME = "app_product_name";
    private static final String DEFINITIONS = "Definition";
    private static final String PACKAGE_YAML_FORMAT = ".yaml";
    private static final String APP_STORE_DOWNLOAD_CSAR = "/mec/appstore/v1/apps/%s/packages/%s/action/download";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String PROTOCOL_APPSTORE = "https://appstore-be-svc:8099";
    private static final String DEPENDENCY_CHECK_FAILED =
            "dependency check failed, pls check appId and packageId exists in appstore.";

    public String execute(String filePath, Map<String, String> context) {
        try {
            Thread.sleep(800);
        } catch (InterruptedException e1) {
        }

        try {
            dependencyCheckSchdule(filePath, new Stack<Map<String, String>>(), context);
        }catch(IllegalArgumentException e) {
            return DEPENDENCY_CHECK_FAILED;
        }
       
        return SUCCESS;
    }

    public void dependencyCheckSchdule(String filePath, Stack<Map<String, String>> dependencyStack,
            Map<String, String> context) {
        List<Map<String, String>> dependencyList = dependencyCheck(filePath);
        if (CollectionUtils.isEmpty(dependencyList)) {
            LOGGER.warn("dependencyCheckSchdule dependencyList is empty.");
            return;
        }
        dependencyStack.addAll(dependencyList);

        dependencyList.forEach(map -> {
            InputStream inputStream = downloadAppFromAppStore(map.get(APP_ID), map.get(PACKAGE_ID), context);
            if (null == inputStream) {
                LOGGER.error("download app from appstore failed.");
                throw new IllegalArgumentException();
            }

            // analysis response and get csar file, get csar file path
            String dependencyFilePath =
                    new StringBuilder().append(getDir()).append(File.separator).append("temp").append(File.separator)
                            .append("dependencyCheck").append(File.separator)
                            .append(map.get(APP_ID)).append(UNDER_LINE).append(map.get(PACKAGE_ID)).toString();
            File file = new File(dependencyFilePath);
            try {
                FileUtils.copyInputStreamToFile(inputStream, file);
                dependencyCheckSchdule(dependencyFilePath, dependencyStack, context);
            } catch (IOException e) {
                String msg = "copy input stream to file failed.";
                LOGGER.error(msg);
                throw new IllegalArgumentException(msg);
            } finally {
                if (!file.delete()) {
                    LOGGER.error("dependencyCheckSchdule file {} delete failed.", file.getName());
                }
            }
        });
    }

    /**
     * dependency application check.
     * 
     * @param filePath filePath
     * @return dependency application info list, contains appName,appId and appPackageId.
     */
    public List<Map<String, String>> dependencyCheck(String filePath) {
        List<Map<String, String>> result = new ArrayList<Map<String, String>>();
        try (ZipFile zipFile = new ZipFile(filePath)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String[] pathSplit = entry.getName().split(SLASH);

                // APPD/Definition/*.yaml
                if (pathSplit.length == 3 && DEFINITIONS.equals(pathSplit[1].trim())
                        && pathSplit[2].trim().endsWith(PACKAGE_YAML_FORMAT)) {
                    analysisDependency(result, zipFile, entry);
                    break;
                }
            }
            LOGGER.info("dependencyCheck end.");
        } catch (Exception e) {
            LOGGER.error("dependency Check failed. {}", e);
        }
        return result;
    }

    private void analysisDependency(List<Map<String, String>> result, ZipFile zipFile, ZipEntry entry) {
        try (BufferedReader br =
                new BufferedReader(new InputStreamReader(zipFile.getInputStream(entry), StandardCharsets.UTF_8))) {
            String line = positionDependencyService(br);
            if (StringUtils.isEmpty(line)) {
                LOGGER.warn("can not find the dependency path, the dependency path must "
                        + "be in node_templates.app_configuration.properties.appServiceRequired");
                return;
            }
            // -serName
            while (line != null && line.trim().startsWith(STRIKE)) {
                LOGGER.info("app_name: {}", line.split(COLON)[1].trim());
                Map<String, String> map = new HashMap<String, String>();
                String appName = line.split(COLON)[1].trim();
                while ((line = br.readLine()) != null && !isEnd(line) && !line.trim().startsWith(STRIKE)) {
                    line = line.trim();
                    if (line.startsWith(APP_ID)) {
                        LOGGER.info("appId: {}", line.split(COLON)[1].trim());
                        map.put(APP_ID, line.split(COLON)[1].trim());
                    } else if (line.startsWith(PACKAGE_ID)) {
                        LOGGER.info("packageid: {}", line.split(COLON)[1].trim());
                        map.put(PACKAGE_ID, line.split(COLON)[1].trim());
                        map.put(APP_NAME, appName);
                    }
                }
                if (map.size() != 0) {
                    LOGGER.info("map is not empty.");
                    result.add(map);
                }
            }

        } catch (IOException e) {
            LOGGER.error("analysis dependency failed. {}", e.getMessage());
        }
    }

    /**
     * position to dependency service field.
     * 
     * @param br bufferReader
     * @return line
     * @throws IOException IOException
     */
    private String positionDependencyService(BufferedReader br) throws IOException {
        String line = "";
        while ((line = br.readLine()) != null) {
            if (line.trim().startsWith(NODE_TEMPLATES)) {
                while ((line = br.readLine()) != null) {
                    if (line.trim().startsWith(APP_CONFIGURATION)) {
                        while ((line = br.readLine()) != null) {
                            if (line.trim().startsWith(PROPERTIES)) {
                                while ((line = br.readLine()) != null) {
                                    if (line.trim().startsWith(APP_SERVICE_REQUIRED)) {
                                        line = br.readLine();
                                        return null == line ? line : line.trim();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return line;
    }

    /**
     * get root dir.
     * 
     * @return root dir
     */
    public static String getDir() {
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            return "C:\\atp";
        } else {
            return "/usr/atp";
        }
    }

    /**
     * download app from appstore.
     * 
     * @param appId appId
     * @param packageId packageId
     * @param context context
     * @return file binary stream
     */
    public InputStream downloadAppFromAppStore(String appId, String packageId, Map<String, String> context) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(ACCESS_TOKEN, context.get(ACCESS_TOKEN));
        HttpEntity<String> request = new HttpEntity<>(headers);

        String url = String.format(APP_STORE_DOWNLOAD_CSAR, appId, packageId);
        LOGGER.info("downloadAppFromAppStore url: {}", url);
        try {
            ResponseEntity<Resource> response =
                    restTemplate.exchange(PROTOCOL_APPSTORE.concat(url), HttpMethod.GET, request, Resource.class);
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
     * if depedency service define end.
     * 
     * @param str yaml line
     * @return is depedency service define end.
     */
    private boolean isEnd(String str) {
        return str.split(COLON).length <= 1;
    }
}
