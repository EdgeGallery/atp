package org.edgegallery.atp.schedule.testcase.sandbox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private RestTemplate restTemplate = new RestTemplate();

    private TestCaseResult testCaseResult = new TestCaseResult();

    @Override
    public TestCaseResult execute(String filePath, Map<String, String> context) {
        Map<String, String> packageInfo = CommonUtil.getPackageInfo(filePath);
        ResponseEntity<String> response =
                CommonUtil.uploadFileToAPM(filePath, context, getMecHost(context), packageInfo);
        if (null == response || !(HttpStatus.OK.equals(response.getStatusCode())
                || HttpStatus.ACCEPTED.equals(response.getStatusCode()))) {
            LOGGER.error("uploadFileToAPM failed, response: {}", response);
            return null == response
                    ? setTestCaseResult(Constant.FAILED, ExceptionConstant.RESPONSE_FROM_APM_FAILED, testCaseResult)
                    : setTestCaseResult(Constant.FAILED,
                            ExceptionConstant.RESPONSE_FROM_APM_FAILED.concat(response.getStatusCode().toString()),
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

        // StringBuffer appInstanceList = new StringBuffer();
        // // analysis dependency app.
        // Stack<Map<String, String>> dependencyAppList = new Stack<Map<String, String>>();
        // CommonUtil.dependencyCheckSchdule(filePath, dependencyAppList, context);
        //
        // LOGGER.info("dependencyAppList: {}", dependencyAppList);
        //
        // // create instantiate
        // List<String> failedAppName = new ArrayList<String>();
        // dependencyAppList.forEach(map -> {
        // InputStream inputStream =
        // CommonUtil.downloadAppFromAppStore(map.get(Constant.APP_ID), map.get(Constant.PACKAGE_ID),
        // context);
        // // analysis response and get csar file, get csar file path
        // String dependencyFilePath = new
        // StringBuilder().append(FileChecker.getDir()).append(File.separator)
        // .append("temp").append(File.separator).append(map.get(Constant.APP_ID)).append(Constant.UNDER_LINE)
        // .append(map.get(Constant.PACKAGE_ID)).append(".csar").toString();
        // LOGGER.warn("dependencyFilePath: {}", dependencyFilePath);
        // File file = new File(dependencyFilePath);
        // try {
        // FileUtils.copyInputStreamToFile(inputStream, file);
        // Map<String, String> pkgInfo = CommonUtil.getPackageInfo(dependencyFilePath);
        // ResponseEntity<String> responsePM =
        // CommonUtil.uploadFileToAPM(dependencyFilePath, context, getMecHost(context), pkgInfo);
        // if (null == responsePM || !(HttpStatus.OK.equals(responsePM.getStatusCode())
        // || HttpStatus.ACCEPTED.equals(response.getStatusCode()))) {
        // LOGGER.error("uploadFileToAPM failed, response: {}", responsePM);
        // }
        // JsonObject jsonObjectDen = new JsonParser().parse(responsePM.getBody()).getAsJsonObject();
        // Map<String, String> appInfoMap = new HashMap<String, String>() {
        // {
        // put(Constant.APP_NAME, pkgInfo.get(Constant.APP_NAME));
        // put(Constant.APP_ID, jsonObjectDen.get("appId").getAsString());
        // put(Constant.PACKAGE_ID, jsonObjectDen.get("appPackageId").getAsString());
        // }
        // };
        //
        // String instanceId = CommonUtil.createInstanceFromAppo(context, appInfoMap, getMecHost(context));
        // if (null == instanceId) {
        // failedAppName.add(map.get(Constant.APP_NAME));
        // } else {
        // appInstanceList.append(instanceId).append(Constant.COMMA);
        // }
        //
        // } catch (IOException e) {
        // String msg = "copy input stream to file failed.";
        // LOGGER.error(msg);
        // throw new IllegalArgumentException(msg);
        // }
        // });
        //
        // LOGGER.info("appInstanceList: {}", appInstanceList.toString());
        //
        // context.put(Constant.DEPENDENCY_APP_INSTANCE_ID, appInstanceList.toString());
        //
        // // some dependence app instantiate failed.
        // if (!CollectionUtils.isEmpty(failedAppName)) {
        // return setTestCaseResult(Constant.FAILED,
        // ExceptionConstant.INSTANTIATE_DEPENDENCE_APP_FAILED.concat(failedAppName.toString()),
        // testCaseResult);
        // }

        // instantiate original app
        String appInstanceId = CommonUtil.createInstanceFromAppo(context, appInfo, getMecHost(context));
        context.put(Constant.APP_INSTANCE_ID, appInstanceId);

        LOGGER.info("original appInstanceId: {}", appInstanceId);

        return null != appInstanceId ? setTestCaseResult(Constant.SUCCESS, Constant.EMPTY, testCaseResult)
                : setTestCaseResult(Constant.FAILED, ExceptionConstant.INSTANTIATE_APP_FAILED, testCaseResult);

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
        headers.set(Constant.ACCESS_TOKEN, context.get(Constant.ACCESS_TOKEN));
        HttpEntity<String> request = new HttpEntity<>(headers);

        String url = Constant.PROTOCOL_INVENTORY
                .concat(String.format(Constant.INVENTORY_GET_MECHOSTS_URL, context.get(Constant.TENANT_ID)));
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

}
