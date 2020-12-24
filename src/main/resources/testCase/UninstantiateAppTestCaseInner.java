
import java.util.Map;
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

    public String execute(String filePath, Map<String, String> context) {
        String appInstanceId = context.get(APP_INSTANCE_ID);
        return deleteAppInstance(appInstanceId, context) ? "success" : UNINSTANTIATE_APP_FAILED;
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


}
