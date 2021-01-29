
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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
    private static final String APP_INSTANCE_ID = "appInstanceId";
    private static final String APP_ID = "appId";
    private static final String PACKAGE_ID = "packageId";
    private static final String SUCCESS = "success";
    private static final String APM_UPLOAD_PACKAGE = "/apm/v1/tenants/%s/packages/upload";
    private static final String PROTOCOL_APM = "https://mecm-apm:8092";
    private static final String PROTOCAL_APPO = "https://mecm-appo:8091";
    private static final String PROTOCOL_INVENTORY = "https://mecm-inventory:8093";
    private static final String PROTOCOL_APPSTORE = "https://appstore-be-svc:8099";
    private static final String INVENTORY_GET_MECHOSTS_URL = "/inventory/v1/tenants/%s/mechosts";
    private static final String TENANT_ID = "tenantId";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPO_CREATE_APPINSTANCE = "/appo/v1/tenants/%s/app_instances";
    private static final String APPLICATION_JSON = "application/json";
    private static final String CREATED = "Created";
    private static final String APPO_INSTANTIATE_APP = "/appo/v1/tenants/%s/app_instances/%s";
    private static final String APPO_GET_INSTANCE = "/appo/v1/tenants/%s/app_instance_infos/%s";
    private static final String PROVIDER_ID = "app_provider_id";
    private static final String INSTANTIATED = "instantiated";
    private RestTemplate restTemplate = new RestTemplate();

    public String execute(String filePath, Map<String, String> context) {
        Map<String, String> packageInfo = getPackageInfo(filePath);
        ResponseEntity<String> response =
                uploadFileToAPM(filePath, context, getMecHost(context), packageInfo);
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

        // instantiate original app
        String appInstanceId = createInstanceFromAppo(context, appInfo, getMecHost(context));
        context.put(APP_INSTANCE_ID, appInstanceId);

        LOGGER.info("original appInstanceId: {}", appInstanceId);

        return null != appInstanceId ? "success" : INSTANTIATE_APP_FAILED;

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
        headers.set(ACCESS_TOKEN, context.get(ACCESS_TOKEN));
        HttpEntity<String> request = new HttpEntity<>(headers);

        String url = PROTOCOL_INVENTORY.concat(String.format(INVENTORY_GET_MECHOSTS_URL, context.get(TENANT_ID)));
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
        LOGGER.info("create instance body: {}", marshal(body));

        HttpHeaders headers = new HttpHeaders();
        headers.set(ACCESS_TOKEN, context.get(ACCESS_TOKEN));
        headers.set(CONTENT_TYPE, APPLICATION_JSON);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        String url = PROTOCAL_APPO.concat(String.format(APPO_CREATE_APPINSTANCE, context.get(TENANT_ID)));

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
                    Thread.sleep(6000);
                    if (getApplicationInstance(context, appInstanceId, CREATED)
                            && instantiateAppFromAppo(context, appInstanceId)) {
                        Thread.sleep(6000);
                        if (getApplicationInstance(context, appInstanceId, INSTANTIATED)) {
                            return appInstanceId;
                        }
                    }
                    return null;
                }
            }
        } catch (RestClientException e) {
            LOGGER.error("Failed to create app instance from appo which appId is {} exception {}",
                    appInfo.get(APP_ID), e.getMessage());
        } catch (InterruptedException e) {
            LOGGER.error("thread sleep failed.");
        }
        return null;
    }

    private String marshal(Object obj) {
        ObjectMapper MAPPER = new ObjectMapper();
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            LOGGER.error("marshal obj failed. {}", obj);
            throw new IllegalArgumentException("marshal obj failed.");
        }
    }

    private boolean getApplicationInstance(Map<String, String> context, String appInstanceId, String status) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(ACCESS_TOKEN, context.get(ACCESS_TOKEN));
        HttpEntity<String> request = new HttpEntity<>(headers);

        String url =
                PROTOCAL_APPO.concat(String.format(APPO_GET_INSTANCE, context.get(TENANT_ID), appInstanceId));

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
     * instantiate application by appo
     * 
     * @param context context info.
     * @param appInstanceId appInstanceId
     * @return instantiate app successful
     */
    private boolean instantiateAppFromAppo(Map<String, String> context, String appInstanceId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(ACCESS_TOKEN, context.get(ACCESS_TOKEN));
        HttpEntity<String> request = new HttpEntity<>(headers);

        String url = PROTOCAL_APPO.concat(String.format(APPO_INSTANTIATE_APP, context.get(TENANT_ID), appInstanceId));
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
                if (entry.getName().split("/").length == 2 && fileSuffixValidate("mf", entry.getName())) {
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

        String url = PROTOCOL_APM.concat(String.format(APM_UPLOAD_PACKAGE, context.get(TENANT_ID)));
        try {
            return restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        } catch (RestClientException e) {
            LOGGER.error("Failed to upload file to apm, exception {}", e.getMessage());
        }
        return null;
    }

    /**
     * validate fileName is .pattern
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