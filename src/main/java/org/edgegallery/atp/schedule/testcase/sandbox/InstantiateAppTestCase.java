package org.edgegallery.atp.schedule.testcase.sandbox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.constant.ExceptionConstant;
import org.edgegallery.atp.model.testcase.TestCaseResult;
import org.edgegallery.atp.schedule.testcase.TestCaseAbs;
import org.edgegallery.atp.utils.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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
        Map<String, String> packageInfo = CommonUtil.getPackageInfo(filePath);
        ResponseEntity<String> response =
                CommonUtil.uploadFileToAPM(
                        filePath, context, appoProtoctol.concat(Constant.COLON).concat(Constant.DOUBLE_SLASH)
                                .concat(appoIp).concat(Constant.COLON).concat(appoPort),
                        getMecHost(context), packageInfo);
        if (null == response || !HttpStatus.OK.equals(response.getStatusCode())) {
            return setTestCaseResult(Constant.Status.FAILED,
                    ExceptionConstant.InstantiateAppTestCase.RESPONSE_FROM_APM_FAILED
                            .concat(response.getStatusCode().toString()),
                    testCaseResult);
        }

        JsonObject jsonObject = new JsonParser().parse(response.getBody()).getAsJsonObject();
        Map<String, String> appInfo = new HashMap<String, String>() {
            {
                put(Constant.APP_NAME, packageInfo.get(Constant.APP_NAME));
                put(Constant.APP_ID, jsonObject.get("appId").getAsString());
                put(Constant.PACKAGE_ID, jsonObject.get("appPackageId").getAsString());
            }
        };

        StringBuffer appInstanceList = new StringBuffer();
        // analysis dependency app.
        Stack<Map<String, String>> dependencyAppList = new Stack<Map<String, String>>();
        CommonUtil.dependencyCheckSchdule(filePath, dependencyAppList);

        List<String> failedAppName = new ArrayList<String>();
        dependencyAppList.forEach(map -> {
            String instanceId = CommonUtil.createInstanceFromAppo(filePath, context, map, getMecHost(context),
                    appoProtoctol.concat(Constant.COLON).concat(Constant.DOUBLE_SLASH).concat(appoIp)
                            .concat(Constant.COLON).concat(appoPort));
            if (null == instanceId) {
                failedAppName.add(map.get(Constant.APP_NAME));
            } else {
                appInstanceList.append(instanceId).append(Constant.COMMA);
            }
        });

        context.put(Constant.DEPENDENCY_APP_INSTANCE_ID, appInstanceList.toString());

        // some dependence app instantiate failed.
        if (!CollectionUtils.isEmpty(failedAppName)) {
            return setTestCaseResult(Constant.Status.FAILED,
                    ExceptionConstant.InstantiateAppTestCase.INSTANTIATE_DEPENDENCE_APP_FAILED
                            .concat(failedAppName.toString()),
                    testCaseResult);
        }

        // instantiate original app
        String appInstanceId = CommonUtil.createInstanceFromAppo(filePath, context, appInfo, getMecHost(context),
                appoProtoctol.concat(Constant.COLON).concat(Constant.DOUBLE_SLASH).concat(appoIp).concat(Constant.COLON)
                        .concat(appoPort));
        context.put(Constant.APP_INSTANCE_ID, appInstanceId);

        return null != appInstanceId ? setTestCaseResult(Constant.Status.SUCCESS, Constant.EMPTY, testCaseResult)
                : setTestCaseResult(Constant.Status.FAILED,
                        ExceptionConstant.InstantiateAppTestCase.INSTANTIATE_APP_FAILED, testCaseResult);

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
