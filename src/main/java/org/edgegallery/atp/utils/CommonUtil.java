package org.edgegallery.atp.utils;

import java.io.BufferedReader;
import java.io.File;
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
import org.edgegallery.atp.utils.file.FileChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CommonUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonUtil.class);

    private static RestTemplate restTemplate = new RestTemplate();

    /**
     * get time according to special format
     * 
     * @return time
     */
    public static String getFormatDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(new Date());
    }

    /**
     * generate uuid randomly
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
        return new File(new StringBuilder().append(FileChecker.getDir()).append(File.separator).append("temp")
                .append(File.separator).append(fileId).append(Constant.UNDER_LINE).append(file.getOriginalFilename())
                .toString()).delete();
    }

    /**
     * get dependency app info from appstore.
     * 
     * @param result key is appName,value is appVersion
     * @param appId appId
     * @param packageId packageId
     * @return
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
     * create app instance from appo.
     * 
     * @param filePath csar file path
     * @param context context info
     * @param appInfo contains appName,appId,appPackageId
     * @param hostIp mec host ip
     * @param ipPort protocl://ip:port
     * @return create app instance sucess or not.s
     */
    public static String createInstanceFromAppo(Map<String, String> context, Map<String, String> appInfo,
            String hostIp) {
        Map<String, Object> body = new HashMap<>();
        body.put("appInstanceDescription", CommonUtil.generateId());
        body.put("appName", appInfo.get(Constant.APP_NAME));
        body.put("appPackageId", appInfo.get(Constant.PACKAGE_ID));
        body.put("appId", appInfo.get(Constant.APP_ID));
        body.put("mecHost", hostIp);
        LOGGER.info("create instance body: {}", JSONUtil.marshal(body));

        HttpHeaders headers = new HttpHeaders();
        headers.set(Constant.ACCESS_TOKEN, context.get(Constant.ACCESS_TOKEN));
        headers.set(Constant.CONTENT_TYPE, Constant.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        String url = Constant.PROTOCAL_APPO
                .concat(String.format(Constant.APPO_CREATE_APPINSTANCE, context.get(Constant.TENANT_ID)));

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
            LOGGER.info("response is: {}", response.getStatusCode());
            if (HttpStatus.OK.equals(response.getStatusCode())
                    || HttpStatus.ACCEPTED.equals(response.getStatusCode())) {
                JsonObject jsonObject = new JsonParser().parse(response.getBody()).getAsJsonObject();
                JsonObject responseBody = jsonObject.get("response").getAsJsonObject();
                if (null != responseBody) {
                    String appInstanceId = responseBody.get("app_instance_id").getAsString();
                    Thread.sleep(5000);
                    if (getApplicationInstance(context, appInstanceId, Constant.CREATED)
                            && instantiateAppFromAppo(context, appInstanceId)) {
                        Thread.sleep(2000);
                        if (getApplicationInstance(context, appInstanceId, Constant.INSTANTIATED)) {
                            return appInstanceId;
                        }
                    }
                    return null;
                }
            }
        } catch (RestClientException e) {
            LOGGER.error("Failed to create app instance from appo which appId is {} exception {}",
                    appInfo.get(Constant.APP_ID), e.getMessage());
        } catch (InterruptedException e) {
            LOGGER.error("thread sleep failed.");
        }
        return null;
    }

    /**
     * instantiate application by appo
     * 
     * @param context context info.
     * @param appInstanceId appInstanceId
     * @return instantiate app successful
     */
    public static boolean instantiateAppFromAppo(Map<String, String> context, String appInstanceId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(Constant.ACCESS_TOKEN, context.get(Constant.ACCESS_TOKEN));
        HttpEntity<String> request = new HttpEntity<>(headers);

        String url = Constant.PROTOCAL_APPO
                .concat(String.format(Constant.APPO_INSTANTIATE_APP, context.get(Constant.TENANT_ID), appInstanceId));
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

    public static boolean getApplicationInstance(Map<String, String> context, String appInstanceId, String status) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(Constant.ACCESS_TOKEN, context.get(Constant.ACCESS_TOKEN));
        HttpEntity<String> request = new HttpEntity<>(headers);

        String url = Constant.PROTOCAL_APPO
                .concat(String.format(Constant.APPO_GET_INSTANCE, context.get(Constant.TENANT_ID), appInstanceId));

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
                if (status.equalsIgnoreCase(responseBody.get("operationalStatus").getAsString())) {
                    LOGGER.info("{} is {}.", appInstanceId, status);
                    break;
                }

                if ((System.currentTimeMillis() - startTime) > 60000) {
                    LOGGER.error("get instance {} from appo time out", appInstanceId);
                    return false;
                }
            } catch (RestClientException e) {
                LOGGER.error("Failed to get application instance from appo which app_instance_id is {} exception {}",
                        appInstanceId, e.getMessage());
                return false;
            }
        }

        return true;
    }

    /**
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
     * delete app instance from appo
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
                if (entry.getName().split(Constant.SLASH).length == 2
                        && TestCaseUtil.fileSuffixValidate("mf", entry.getName())) {
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
    public static ResponseEntity<String> uploadFileToAPM(String filePath, Map<String, String> context, String hostIp,
            Map<String, String> packageInfo) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(filePath));
        body.add("hostList", hostIp);
        body.add("appPackageName", packageInfo.get(Constant.APP_NAME));
        body.add("appPackageVersion", packageInfo.get(Constant.APP_VERSION));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set(Constant.ACCESS_TOKEN, context.get(Constant.ACCESS_TOKEN));

        LOGGER.info("hostIp: " + hostIp);
        LOGGER.info("appPackageName: " + packageInfo.get(Constant.APP_NAME));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        String url = Constant.PROTOCOL_APM
                .concat(String.format(Constant.APM_UPLOAD_PACKAGE, context.get(Constant.TENANT_ID)));
        try {
            return restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        } catch (RestClientException e) {
            LOGGER.error("Failed to upload file to apm, exception {}", e.getMessage());
        }
        return null;
    }

    /**
     * uuid validate
     * 
     * @param param parameter
     * @return is legal uuid pattern
     */
    public static void isUuidPattern(String param) {
        Pattern pattern = Pattern.compile("[0-9a-f]{8}(-[0-9a-f]{4}){3}-[0-9a-f]{12}");
        if (!pattern.matcher(param).matches()) {
            throw new IllegalArgumentException(String.format("%s is not uuid pattern.", param));
        }
    }

    /**
     * name validate
     * 
     * @param name name
     * @return is legal name pattern
     */
    public static void lengthCheck(String param) {
        if (!StringUtils.isEmpty(param) && param.length() > 128) {
            throw new IllegalArgumentException(String.format("the length of %s must less than 128.", param));
        }
    }

    /**
     * validate context is not empty.
     */
    public static void validateContext() {
        if (null == AccessTokenFilter.context.get()) {
            throw new IllegalArgumentException(ExceptionConstant.CONTEXT_IS_NULL);
        }
    }
}
