package org.edgegallery.atp.schedule.testcase.sandbox;

import java.util.Map;
import java.util.UUID;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.constant.ExceptionConstant;
import org.edgegallery.atp.model.testcase.TestCaseResult;
import org.edgegallery.atp.schedule.testcase.TestCaseAbs;
import org.edgegallery.atp.utils.TestCaseUtil;
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
        body.add("hostIp", TestCaseUtil.getMecHost(context));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set(Constant.ACCESS_TOKEN, context.get(Constant.ACCESS_TOKEN));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        // TODO IP PORT
        String url =
                "https://ip:port" + Constant.URL.APP_LCM_INSTANTIATE_APP_URL.replaceAll("appInstanceId", appInstanceId);
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

}
