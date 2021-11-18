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

package org.edgegallery.atp.constant;

import java.io.File;
import org.edgegallery.atp.utils.FileChecker;

public interface Constant {

    String EMPTY = "";

    String TASK_ID = "taskId";

    String UNDER_LINE = "_";

    String PROVIDER_ID = "app_provider_id";

    String APP_NAME = "app_product_name";

    String APP_VERSION = "app_package_version";

    String ARCHITECTURE = "app_architecture";

    String ACCESS_TOKEN = "access_token";

    String USER_ID = "userId";

    String USER_NAME = "userName";

    String TENANT_ID = "tenantId";

    String DOT = ".";

    String COLON = ":";

    String SLASH = "/";

    String COMMA = ",";

    String SUCCESS = "success";

    String RUNNING = "running";

    String FAILED = "failed";

    String WAITING = "waiting";

    String ATP_CREATED = "created";

    String WORK_TEMP_DIR = FileChecker.getDir() + File.separator + "/file/temp";

    String REG = "[^\\s\\\\/:*?\"<>|](\\x20|[^\\s\\\\/:*?\"<>|])*[^\\s\\\\/:*?\"<>|.]$";

    int MAX_LENGTH_FILE_NAME = 255;

    int BUFFER = 512;

    long TOO_BIG = 0x280000000L; // max size of unzipped data, 10GB

    int TOO_MANY = 1024; // max number of files

    String APPLICATION_JSON = "application/json";

    String JAVA = "java";

    String PYTHON = "python";

    String JAR = "jar";

    String TEST_CASE_DIR = "testCases";

    String LOCALE_CH = "ch";

    String LOCALE_EN = "en";

    String FILE_TYPE_SCENARIO = "scenario";

    String BASIC_TEST_CASE_PATH = FileChecker.getDir() + "/file/testCase/";

    String BASIC_ICON_PATH = FileChecker.getDir() + "/file/icon/";

    String BASIC_CONTRIBUTION_PATH = FileChecker.getDir() + "/file/contribution/";

    String TEMP_FILE_PATH = FileChecker.getDir() + "/temp/";

    String ICON = "icon";

    String TASK_TYPE_MANUAL = "manual";

    String TASK_TYPE_AUTOMATIC = "automatic";

    String CONTRIBUTION_TYPE_SCRIPT = "script";

    int LENGTH_64 = 64;

    int LENGTH_255 = 255;

    int LENGTH_2000 = 2000;

    String REG_ID = "[0-9a-f]{8}(-[0-9a-f]{4}){3}-[0-9a-f]{12}";

    String ZIP = ".zip";

    String ID = "id";

    String NAME_EN = "nameEn";

    String TYPE = "type";

    String ERROR_CODE = "errCode";

    String ERROR_MSG = "errMsg";

    String PARAMS = "params";

    String TEST_SCENARIO = "testScenario";

    String TEST_SUITE = "testSuite";

    String TEST_CASE = "testCase";

    String APM_SERVER_ADDRESS = "apmServerAddress";

    String APPO_SERVER_ADDRESS = "appoServerAddress";

    String INVENTORY_SERVER_ADDRESS = "inventoryServerAddress";

    String APPSTORE_SERSVER_ADDRESS = "appstoreServerAddress";

    String SEMICOLON = ";";

    String EQUAL_MARK = "=";

    String CONFIG_PARAM_LIST = "configParamList";

    String SIGNATURE_RESULT = "signatureResult";

    String CONFIG_ID = "config id";

    String TEST_CASE_ID = "test case id";
}
