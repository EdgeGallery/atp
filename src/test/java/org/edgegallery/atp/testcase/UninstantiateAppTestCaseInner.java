package org.edgegallery.atp.testcase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
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
 *
 */
public class UninstantiateAppTestCaseInner {

    private static final Logger LOGGER = LoggerFactory.getLogger(UninstantiateAppTestCaseInner.class);

    private static final String UNINSTANTIATE_APP_FAILED =
            "delete instantiate app from appo failed, the appInstanceId is: ";

    private static RestTemplate restTemplate = new RestTemplate();

    private static final String APPO_DELETE_APPLICATION_INSTANCE = "/appo/v1/tenants/%s/app_instances/%s";

    private static final String PROTOCAL_APPO = "https://mecm-appo:8091";

    private static final String ACCESS_TOKEN = "access_token";

    private static final String TENANT_ID = "tenantId";

    private static final String APP_INSTANCE_ID = "appInstanceId";

    private static final String VM = "vm";

    private static final String SUCCESS = "success";

    public String execute(String filePath, Map<String, String> context) {
        if (VM.equalsIgnoreCase(getAppType(filePath))) {
            LOGGER.info("delete instantce--package is vm, return success.");
            return SUCCESS;
        }
        String appInstanceId = context.get(APP_INSTANCE_ID);
        return deleteAppInstance(appInstanceId, context) ? SUCCESS : UNINSTANTIATE_APP_FAILED;
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

        String url = PROTOCAL_APPO
                .concat(String.format(APPO_DELETE_APPLICATION_INSTANCE, context.get(TENANT_ID), appInstanceId));
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
     * get app_type
     * 
     * @param filePath filePath
     * @return appType
     */
    private String getAppType(String filePath) {
        try (ZipFile zipFile = new ZipFile(filePath)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.getName().split("/").length == 1 && entry.getName().endsWith(".mf")) {
                    try (BufferedReader br = new BufferedReader(
                            new InputStreamReader(zipFile.getInputStream(entry), StandardCharsets.UTF_8))) {
                        String line = "";
                        while ((line = br.readLine()) != null) {
                            // prefix: path
                            if (line.trim().startsWith("app_class")) {
                                return line.split(":")[1].trim();
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("getPackageInfo failed. {}", e.getMessage());
        }
        LOGGER.warn("app_class field is null.");
        return null;
    }

}
