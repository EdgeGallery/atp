package org.edgegallery.atp.constant;

import java.io.File;
import org.edgegallery.atp.utils.file.FileChecker;

public interface Constant {

    String EMPTY = "";

    String TASK_ID = "taskId";

    String UNDER_LINE = "_";

    int MAX_TASK_THREAD_NUM = 10;

    String PROVIDER_ID = "app_provider_id";

    String APP_NAME = "app_product_name";

    String APP_VERSION = "app_package_version";

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

    String WORK_TEMP_DIR = FileChecker.getDir() + File.separator + "temp";

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

    int TOO_BIG = 0x6400000; // max size of unzipped data, 100MB

    int TOO_MANY = 1024; // max number of files

    String VIRUS_SCAN_TEST = "virusScanningTest";

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

    String PROTOCOL_APM = "https://mecm-apm:8092";

    String PROTOCAL_APPO = "https://mecm-appo:8091";

    String PROTOCOL_INVENTORY = "https://mecm-inventory:8093";

    String PROTOCOL_APPSTORE = "https://appstore-be-svc:8099";

    String CONTENT_TYPE = "Content-Type";

    String APPLICATION_JSON = "application/json";

    String APPO_GET_INSTANCE = "/appo/v1/tenants/%s/app_instance_infos/%s";

    String CREATED = "Created";

    String APPO_INSTANTIATE_APP = "/appo/v1/tenants/%s/app_instances/%s";

    String INSTANTIATED = "instantiated";

    String INSTANTIATE_FAILED = "Instantiation failed";

    String CREATED_FAILED = "Create failed";
}
