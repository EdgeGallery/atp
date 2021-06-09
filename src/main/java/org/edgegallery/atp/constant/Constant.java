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

    int MAX_TASK_THREAD_NUM = 10;

    String PROVIDER_ID = "app_provider_id";

    String APP_NAME = "app_product_name";

    String APP_VERSION = "app_package_version";

    String ARCHITECTURE = "app_architecture";

    String ACCESS_TOKEN = "access_token";

    String USER_ID = "userId";

    String USER_NAME = "userName";

    String TENANT_ID = "tenantId";

    String IP = "ip";

    String PORT = "port";

    String DOT = ".";

    String COLON = ":";

    String DOUBLE_SLASH = "//";

    String SLASH = "/";

    String COMMA = ",";

    String STRIKE = "-";

    String APP_INSTANCE_ID = "appInstanceId";

    String DEPENDENCY_APP_INSTANCE_ID = "dependencyAppInstanceId";

    String DEFINITIONS = "Definition";

    String MAIN_SERVICE_TEMPLATE_YAML = "MainServiceTemplate.yaml";

    String DEPENDENCE = "dependencies";

    String APP_ID = "appId";

    String PACKAGE_ID = "packageId";

    String SUCCESS = "success";

    String RUNNING = "running";

    String FAILED = "failed";

    String WAITING = "waiting";

    String ATP_CREATED = "created";

    String ATP_CREATED_FAILED = "create failed";

    String WORK_TEMP_DIR = FileChecker.getDir() + File.separator + "/file/temp";

    String REG = "[^\\s\\\\/:*?\"<>|](\\x20|[^\\s\\\\/:*?\"<>|])*[^\\s\\\\/:*?\"<>|.]$";

    int MAX_LENGTH_FILE_NAME = 255;

    String PACKAGE_XML_FORMAT = ".xml";

    String PACKAGE_YAML_FORMAT = ".yaml";

    String PACKAGE_CSH_FORMAT = ".csh";

    String PACKAGE_META_FORMAT = ".meta";

    String PACKAGE_TXT_FORMAT = ".txt";

    String MANIFEST = ".mf";

    String MARK_DOWN = ".md";

    int BUFFER = 512;

    long TOO_BIG = 0x280000000L; // max size of unzipped data, 10GB

    int TOO_MANY = 1024; // max number of files

    String SECURITY_TEST = "securityTest";

    String COMPLIANCE_TEST = "complianceTest";

    String SANDBOX_TEST = "sandboxTest";

    String INVENTORY_GET_MECHOSTS_URL = "/inventory/v1/tenants/%s/mechosts";

    String APP_STORE_GET_APP_PACKAGE = "/mec/appstore/v1/apps/%s/packages/%s";

    String APP_STORE_DOWNLOAD_CSAR = "/mec/appstore/v1/apps/%s/packages/%s/action/download";

    String APM_UPLOAD_PACKAGE = "/apm/v1/tenants/%s/packages/upload";

    String APPO_CREATE_APPINSTANCE = "/appo/v1/tenants/%s/app_instances";

    String APPO_DELETE_APPLICATION_INSTANCE = "/appo/v1/tenants/%s/app_instances/%s";

    String NODE_TEMPLATES = "node_templates";

    String APP_CONFIGURATION = "app_configuration";

    String PROPERTIES = "properties";

    String APP_SERVICE_REQUIRED = "appServiceRequired";

    String SERNAME = "serName";

    String CONTENT_TYPE = "Content-Type";

    String APPLICATION_JSON = "application/json";

    String APPO_GET_INSTANCE = "/appo/v1/tenants/%s/app_instance_infos/%s";

    String CREATED = "Created";

    String APPO_INSTANTIATE_APP = "/appo/v1/tenants/%s/app_instances/%s";

    String INSTANTIATED = "instantiated";

    String INSTANTIATE_FAILED = "Instantiation failed";

    String CREATED_FAILED = "Create failed";

    String JAVA = "java";

    String PYTHON = "python";

    String JAR = "jar";

    String JAVA_FILE = ".java";

    String EDGE_GALLERY = "EdgeGallery";

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
    
    String CONTRIBUTION_TYPE_TEXT = "text";
    
    String CONTRIBUTION_TYPE_SCRIPT = "script";

    int LENGTH_64 = 64;

    int LENGTH_255 = 255;

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
}
