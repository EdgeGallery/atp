package org.edgegallery.atp.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.interfaces.filter.AccessTokenFilter;
import org.edgegallery.atp.utils.file.FileChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static RestTemplate REST_TEMPLATE = new RestTemplate();

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
    public static void deleteTempFile(String fileId, MultipartFile file) {
        new File(new StringBuilder().append(FileChecker.getDir()).append(File.separator).append("temp")
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

        String url = String.format(Constant.URL.APP_STORE_GET_APP_PACKAGE, appId, packageId);
        try {
            ResponseEntity<String> response = REST_TEMPLATE.exchange(url, HttpMethod.GET, request, String.class);
            if (!HttpStatus.OK.equals(response.getStatusCode())) {
                LOGGER.error("get app info from appstore reponse failed. The status code is {}",
                        response.getStatusCode());
                return null;
            }

            JsonObject jsonObject = new JsonParser().parse(response.getBody()).getAsJsonObject();
            return jsonObject;
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
    public static JsonObject downloadAppFromAppStore(String appId, String packageId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(Constant.ACCESS_TOKEN, AccessTokenFilter.context.get().get(Constant.ACCESS_TOKEN));
        HttpEntity<String> request = new HttpEntity<>(headers);

        String url = String.format(Constant.URL.APP_STORE_DOWNLOAD_CSAR, appId, packageId);
        try {
            ResponseEntity<String> response = REST_TEMPLATE.exchange(url, HttpMethod.GET, request, String.class);
            if (!HttpStatus.OK.equals(response.getStatusCode())) {
                LOGGER.error("download csar file from appstore reponse failed. The status code is {}",
                        response.getStatusCode());
                return null;
            }

            JsonObject jsonObject = new JsonParser().parse(response.getBody()).getAsJsonObject();
            return jsonObject;
        } catch (RestClientException e) {
            LOGGER.error("Failed to get app info from appstore which appId is {} exception {}", appId, e.getMessage());
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
    public static String createInstanceFromAppo(String filePath, Map<String, String> context,
            Map<String, String> appInfo, String hostIp, String ipPort) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("appInstanceDescription", CommonUtil.generateId());
        body.add("appName", appInfo.get(Constant.APP_NAME));
        body.add("appPackageId", appInfo.get(Constant.PACKAGE_ID));
        body.add("appdId", appInfo.get(Constant.APP_ID));
        body.add("mecHost", hostIp);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set(Constant.ACCESS_TOKEN, context.get(Constant.ACCESS_TOKEN));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        String url =
                ipPort.concat(String.format(Constant.URL.APPO_CREATE_APPINSTANCE, context.get(Constant.TENANT_ID)));
        try {
            ResponseEntity<String> response = REST_TEMPLATE.exchange(url, HttpMethod.POST, requestEntity, String.class);

            if (HttpStatus.OK.equals(response.getStatusCode())) {
                JsonObject jsonObject = new JsonParser().parse(response.getBody()).getAsJsonObject();
                return jsonObject.get("app_instance_id").getAsString();
            }
        } catch (RestClientException e) {
            LOGGER.error("Failed to create app instance from appo which appId is {} exception {}",
                    appInfo.get(Constant.APP_ID), e.getMessage());
        }
        return null;
    }

    /**
     * 
     * @param filePath csar file path
     * @param dependencyStack stack contains all dependency app.
     */
    public static void dependencyCheckSchdule(String filePath, Stack<Map<String, String>> dependencyStack) {
        List<Map<String, String>> dependencyList = FileChecker.dependencyCheck(filePath);
        if (CollectionUtils.isEmpty(dependencyList)) {
            return;
        }
        dependencyStack.addAll(dependencyList);

        dependencyList.forEach(map -> {
            JsonObject response = downloadAppFromAppStore(map.get(Constant.APP_ID), map.get(Constant.PACKAGE_ID));
            if (null != response) {
                // TODO analysis response and get csar file, get csar file path
                String dependencyFilePath = "";
                dependencyCheckSchdule(dependencyFilePath, dependencyStack);
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

        String url = String.format(Constant.URL.APPO_DELETE_APPLICATION_INSTANCE, context.get(Constant.TENANT_ID),
                appInstanceId);
        try {
            ResponseEntity<String> response = REST_TEMPLATE.exchange(url, HttpMethod.DELETE, request, String.class);
            if (HttpStatus.OK.equals(response.getStatusCode())) {
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
}
