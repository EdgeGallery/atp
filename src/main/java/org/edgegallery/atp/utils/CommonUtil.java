/*
 * Copyright 2020-2021 Huawei Technologies Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.edgegallery.atp.utils;

import com.alibaba.fastjson.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.commons.lang3.StringUtils;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.constant.ErrorCode;
import org.edgegallery.atp.constant.ExceptionConstant;
import org.edgegallery.atp.interfaces.filter.AccessTokenFilter;
import org.edgegallery.atp.model.BatchOpsRes;
import org.edgegallery.atp.model.task.testscenarios.TaskTestCase;
import org.edgegallery.atp.utils.exception.FileNotExistsException;
import org.edgegallery.atp.utils.exception.IllegalRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

public class CommonUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonUtil.class);

    private CommonUtil() {

    }

    /**
     * get time according to special format.
     *
     * @return time
     */
    public static String getFormatDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(new Date());
    }

    /**
     * generate uuid randomly.
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
    public static boolean deleteTempFile(String fileId, MultipartFile file) {
        return new File(new StringBuilder().append(Constant.WORK_TEMP_DIR).append(File.separator).append(fileId)
            .append(Constant.UNDER_LINE).append(file.getOriginalFilename()).toString()).delete();
    }

    /**
     * delete file according to filepath.
     *
     * @param filePath file path
     */
    public static void deleteFile(String filePath) {
        if (!new File(filePath).delete()) {
            LOGGER.error("delete file failed.");
        }
    }

    /**
     * delete file.
     *
     * @param file file
     */
    public static void deleteFile(File file) {
        if (!file.delete()) {
            LOGGER.error("delete file failed.");
        }
    }

    /**
     * get package info from csar file.
     *
     * @param filePath file path
     * @return package info
     */
    public static Map<String, String> getPackageInfo(String filePath) {
        Map<String, String> packageInfo = new HashMap<String, String>();
        try (ZipFile zipFile = new ZipFile(filePath)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.getName().split(Constant.SLASH).length == 1 && checkFileSuffix("mf", entry.getName())) {
                    try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(zipFile.getInputStream(entry), StandardCharsets.UTF_8))) {
                        String line = "";
                        while ((line = br.readLine()) != null) {
                            // prefix: path
                            if (line.trim().startsWith(Constant.APP_NAME)) {
                                packageInfo.put(Constant.APP_NAME, line.split(Constant.COLON)[1].trim());
                            }
                            if (line.trim().startsWith(Constant.APP_VERSION)) {
                                packageInfo.put(Constant.APP_VERSION, line.split(Constant.COLON)[1].trim());
                            }
                            if (line.trim().startsWith(Constant.PROVIDER_ID)) {
                                packageInfo.put(Constant.PROVIDER_ID, line.split(Constant.COLON)[1].trim());
                            }
                        }
                    }
                }

                if (entry.getName().split(Constant.SLASH).length == 2 && "SwImageDesc.json"
                    .equals(entry.getName().substring(entry.getName().lastIndexOf(Constant.SLASH) + 1))) {
                    try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(zipFile.getInputStream(entry), StandardCharsets.UTF_8))) {
                        String line = "";
                        while ((line = br.readLine()) != null) {
                            // prefix: path
                            if (line.trim().startsWith("\"architecture\"")) {
                                String architecture = line.split(Constant.COLON)[1].trim();
                                architecture = architecture.replaceAll("[\",]", "");
                                packageInfo.put(Constant.ARCHITECTURE, architecture);
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
     * uuid validate.
     *
     * @param param parameter
     */
    public static void isUuidPattern(String param) {
        Pattern pattern = Pattern.compile(Constant.REG_ID);
        if (!pattern.matcher(param).matches()) {
            LOGGER.error("param is not uuid pattern.");
            throw new IllegalArgumentException(String.format("%s is not uuid pattern.", param));
        }
    }

    /**
     * validate context is not empty.
     */
    public static void validateContext() {
        if (null == AccessTokenFilter.CONTEXT.get()) {
            LOGGER.error("context is null.");
            throw new IllegalArgumentException(ExceptionConstant.CONTEXT_IS_NULL);
        }
    }

    /**
     * set test case result according to response.
     *
     * @param response execute response result
     * @param taskTestCase taskTestCase
     */
    public static void setResult(Object response, TaskTestCase taskTestCase) {
        if (null == response) {
            LOGGER.error(ExceptionConstant.METHOD_RETURN_IS_NULL);
            taskTestCase.setResult(Constant.FAILED);
            taskTestCase.setReason(ExceptionConstant.METHOD_RETURN_IS_NULL);
        } else if (Constant.SUCCESS.equalsIgnoreCase(response.toString())) {
            taskTestCase.setResult(Constant.SUCCESS);
        } else {
            taskTestCase.setResult(Constant.FAILED);
            taskTestCase.setReason(response.toString());
        }
    }

    /**
     * validate fileName is .pattern
     *
     * @param pattern filePattern
     * @param fileName fileName
     * @return
     */
    public static boolean checkFileSuffix(String pattern, String fileName) {
        String suffix = fileName.substring(fileName.lastIndexOf(Constant.DOT) + 1, fileName.length());
        return StringUtils.isNotBlank(suffix) && suffix.equals(pattern);
    }

    /**
     * get java class name.
     *
     * @param file java file
     * @return class name
     */
    public static String getClassPath(File file) {
        String className = Constant.EMPTY;
        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(new FileInputStream(file.getCanonicalPath()), StandardCharsets.UTF_8))) {
            String line = "";
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("public class")) {
                    String[] arr = line.split("\\s+");
                    className = arr[2];
                    LOGGER.info("className: {}", className);
                    break;
                }
            }

            return className;
        } catch (IOException e) {
            LOGGER.error("get class path failed.");
            throw new IllegalArgumentException("get class path failed.");
        }
    }

    /**
     * set fail response body.
     *
     * @param id id
     * @param nameEn nameEn
     * @param type testScenario or testCase or testSuite
     * @param errCode errCode
     * @param errMsg errMsg
     * @param params params
     * @return fail response body
     */
    public static JSONObject setFailureRes(String id, String nameEn, String type, int errCode, String errMsg,
        List<String> params) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.ID, id);
        jsonObject.put(Constant.NAME_EN, nameEn);
        jsonObject.put(Constant.TYPE, type);
        jsonObject.put(Constant.ERROR_CODE, errCode);
        jsonObject.put(Constant.ERROR_MSG, errMsg);
        jsonObject.put(Constant.PARAMS, params);
        return jsonObject;
    }

    /**
     * set batch delete failed response body.
     *
     * @param result failed id list
     * @return batch operation response body
     */
    public static BatchOpsRes setBatchDeleteFailedRes(Map<String, List<String>> result) {
        List<String> failed = result.get(Constant.FAILED);
        List<JSONObject> failures = new ArrayList<>();
        if (!CollectionUtils.isEmpty(failed)) {
            failed.forEach(failId -> {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(Constant.ID, failId);
                jsonObject.put(Constant.ERROR_CODE, ErrorCode.DB_ERROR);
                jsonObject.put(Constant.ERROR_MSG, ErrorCode.DB_ERROR_MSG);
                jsonObject.put(Constant.PARAMS, null);
                failures.add(jsonObject);
            });
        }

        return new BatchOpsRes(ErrorCode.RET_CODE_SUCCESS, null, failures);
    }

    /**
     * set param value, if param equal null, return defaultValue.
     *
     * @param param param
     * @param defaultValue defaultValue
     * @return param
     */
    public static String setParamOrDefault(String param, String defaultValue) {
        return null == param ? defaultValue : param;
    }

    /**
     * param length not bigger than length.
     *
     * @param param param
     * @param length standard length
     * @return param length not bigger than length
     */
    public static boolean isLengthOk(String param, int length) {
        if (param.length() > length) {
            LOGGER.error("param {} size can not longer than {}", param, length);
            return false;
        }
        return true;
    }

    /**
     * validate nameCh and nameEn existence.
     *
     * @param nameCh chinese name
     * @param nameEn english name
     */
    public static void nameNotEmptyValidation(String nameCh, String nameEn) {
        if (StringUtils.isEmpty(nameCh) && StringUtils.isEmpty(nameEn)) {
            LOGGER.error("nameCh and nameEn both not exist.");
            throw new IllegalRequestException(String.format(ErrorCode.PARAM_IS_NULL_MSG, "nameCh and nameEn both"),
                ErrorCode.PARAM_IS_NULL, new ArrayList<String>(Arrays.asList("nameCh and nameEn both")));
        }
    }

    /**
     * check file is not empty.
     *
     * @param file file
     * @return file is not empty
     */
    public static boolean checkFileNotEmpty(MultipartFile file) {
        return null != file && StringUtils.isNotBlank(file.getOriginalFilename()) && StringUtils
            .isNotBlank(file.getName()) && 0 != (int) file.getSize();
    }

    /**
     * validate whether param is empty or not.
     *
     * @param param param
     * @param msg error msg
     * @param <T> param type
     */
    public static <T> void checkParamEmpty(T param, String msg) {
        if (null == param) {
            LOGGER.error(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * validate whether param is empty or not in new error code pattern.
     *
     * @param param param
     * @param errorMsg errorMsg
     * @param errorParam errorParam
     * @param <T> param type
     */
    public static <T> void checkParamEmpty(T param, String errorMsg, String errorParam) {
        if (null == param) {
            LOGGER.error(errorMsg);
            throw new IllegalRequestException(String.format(ErrorCode.NOT_FOUND_EXCEPTION_MSG, errorParam),
                ErrorCode.NOT_FOUND_EXCEPTION, new ArrayList<String>(Arrays.asList(errorParam)));
        }
    }

    /**
     * validate entity is not found.
     *
     * @param param param
     * @param msg error msg
     * @param <T> param type
     * @throws FileNotFoundException FileNotFoundException
     */
    public static <T> void checkEntityNotFound(T param, String msg) throws FileNotFoundException {
        if (null == param) {
            LOGGER.error(msg);
            throw new FileNotFoundException(msg);
        }
    }

    /**
     * validate entity is not found in new error code pattern.
     *
     * @param object object
     * @param errorParam errorParam
     * @param errorMsg errorMsg
     * @param <T> object type
     * @throws FileNotExistsException FileNotExistsException
     */
    public static <T> void checkEntityNotFound(T object, String errorMsg, String errorParam)
        throws FileNotExistsException {
        if (null == object) {
            LOGGER.error(errorMsg);
            throw new FileNotExistsException(String.format(ErrorCode.NOT_FOUND_EXCEPTION_MSG, errorParam),
                ErrorCode.NOT_FOUND_EXCEPTION, new ArrayList<String>(Arrays.asList(errorParam)));
        }
    }
}
