package org.edgegallery.atp.schedule.testcase.sandbox;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.constant.ExceptionConstant;
import org.edgegallery.atp.model.testcase.TestCaseResult;
import org.edgegallery.atp.schedule.testcase.TestCaseAbs;
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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * Instantiate app.
 *
 */
public class InstantiateAppTestCase extends TestCaseAbs {

    private static final Logger LOGGER = LoggerFactory.getLogger(InstantiateAppTestCase.class);

    private RestTemplate REST_TEMPLATE = new RestTemplate();

    private TestCaseResult testCaseResult = new TestCaseResult();

    @Override
    public TestCaseResult execute(String filePath, Map<String, String> context) {
        String appInstanceId = UUID.randomUUID().toString();
        context.put(Constant.APP_INSTANCE_ID, appInstanceId);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(filePath));
        body.add("hostIp", getMecHost(context));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set(Constant.ACCESS_TOKEN, context.get(Constant.ACCESS_TOKEN));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        String url = applcmProtoctol.concat(Constant.COLON).concat(Constant.DOUBLE_SLASH).concat(applcmIp)
                .concat(Constant.COLON).concat(applcmPort)
                .concat(Constant.URL.APP_LCM_INSTANTIATE_APP_URL.replaceAll("appInstanceId", appInstanceId));
        try {
            ResponseEntity<String> response = REST_TEMPLATE.exchange(url, HttpMethod.POST, requestEntity, String.class);

            return HttpStatus.OK.equals(response.getStatusCode())
                    ? setTestCaseResult(Constant.Status.SUCCESS, Constant.EMPTY, testCaseResult)
                    : setTestCaseResult(Constant.Status.FAILED,
                            ExceptionConstant.InstantiateAppTestCase.RESPONSE_FROM_APPLCM_FAILED
                                    .concat(response.getStatusCode().toString()),
                            testCaseResult);

        } catch (RestClientException e) {
            LOGGER.error("Failed to instantiate application which appInstanceId is {} exception {}", appInstanceId,
                    e.getMessage());
            return setTestCaseResult(Constant.Status.FAILED,
                    ExceptionConstant.InstantiateAppTestCase.SEND_REQUEST_TO_APPLCM_FAILED, testCaseResult);
        }
    }

    /**
     * send request to inventory to get mecHost ip.
     * 
     * @param context context info
     * @return mecHostIp
     */
    private String getMecHost(Map<String, String> context) {
        String appInstanceId = UUID.randomUUID().toString();
        List<String> mecHostIpList = new ArrayList<String>();

        HttpHeaders headers = new HttpHeaders();
        headers.set(Constant.ACCESS_TOKEN, context.get(Constant.ACCESS_TOKEN));
        HttpEntity<String> request = new HttpEntity<>(headers);

        String url = inventoryProtoctol.concat(Constant.COLON).concat(Constant.DOUBLE_SLASH).concat(inventoryIp)
                .concat(Constant.COLON).concat(inventoryPort).concat(Constant.URL.INVENTORY_GET_MECHOSTS_URL
                        .replaceAll(Constant.TENANT_ID, context.get(Constant.TENANT_ID)));
        try {
            ResponseEntity<String> response = REST_TEMPLATE.exchange(url, HttpMethod.GET, request, String.class);
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
            LOGGER.error("Failed to instantiate application which appInstanceId is {} exception {}", appInstanceId,
                    e.getMessage());
            return null;
        }

        return mecHostIpList.get(0);
    }

}
