package org.edgegallery.atp.schedule.testcase.sandbox;

import java.util.Map;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.constant.ExceptionConstant;
import org.edgegallery.atp.model.testcase.TestCaseResult;
import org.edgegallery.atp.schedule.testcase.TestCaseAbs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * terminate app instance.
 *
 */
public class UninstantiateAppTestCase extends TestCaseAbs {
    private static final Logger LOGGER = LoggerFactory.getLogger(UninstantiateAppTestCase.class);

    private RestTemplate REST_TEMPLATE = new RestTemplate();

    private TestCaseResult testCaseResult = new TestCaseResult();

    @Override
    public TestCaseResult execute(String filePath, Map<String, String> context) {
        String appInstanceId = context.get(Constant.APP_INSTANCE_ID);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(Constant.ACCESS_TOKEN, context.get(Constant.ACCESS_TOKEN));

        String url = applcmProtoctol.concat(Constant.COLON).concat(Constant.DOUBLE_SLASH).concat(applcmIp)
                .concat(Constant.COLON).concat(applcmPort)
                .concat(Constant.URL.APP_LCM_TERMINATE_APP_URL.replaceAll("appInstanceId", appInstanceId));
        try {
            ResponseEntity<String> response =
                    REST_TEMPLATE.exchange(url, HttpMethod.POST, new HttpEntity<>(headers), String.class);

            return HttpStatus.OK.equals(response.getStatusCode())
                    ? setTestCaseResult(Constant.Status.SUCCESS, Constant.EMPTY, testCaseResult)
                    : setTestCaseResult(Constant.Status.FAILED,
                            ExceptionConstant.UninstantiateAppTestCase.RESPONSE_FROM_APPLCM_FAILED
                                    .concat(response.getStatusCode().toString()),
                            testCaseResult);
        } catch (RestClientException e) {
            LOGGER.error("Failed to terminate app instance which appInstanceId is {} exception {}", appInstanceId,
                    e.getMessage());
            return setTestCaseResult(Constant.Status.FAILED,
                    ExceptionConstant.UninstantiateAppTestCase.SEND_REQUEST_TO_APPLCM_FAILED, testCaseResult);
        }
    }

}
