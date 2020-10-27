package org.edgegallery.atp.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import com.google.gson.JsonElement;
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
    public static Map<String, String> getAppInfoFromAppStore(Map<String, String> result, String appId,
            String packageId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(Constant.ACCESS_TOKEN, AccessTokenFilter.context.get().get(Constant.ACCESS_TOKEN));
        HttpEntity<String> request = new HttpEntity<>(headers);

        String url = String.format(Constant.URL.APP_STORE_GET_APP_PACKAGE, appId, packageId);
        try {
            ResponseEntity<String> response = REST_TEMPLATE.exchange(url, HttpMethod.GET, request, String.class);
            if (!HttpStatus.OK.equals(response.getStatusCode())) {
                LOGGER.error("get app info from appstore reponse failed. The status code is {}",
                        response.getStatusCode());
                return result;
            }

            JsonObject jsonObject = new JsonParser().parse(response.getBody()).getAsJsonObject();
            JsonElement appName = jsonObject.get("name");
            JsonElement appVersion = jsonObject.get("version");
            result.put(appName.getAsString(), appVersion.getAsString());
        } catch (RestClientException e) {
            LOGGER.error("Failed to get app info from appstore which appId is {} exception {}", appId, e.getMessage());
        }
        return result;
    }
}
