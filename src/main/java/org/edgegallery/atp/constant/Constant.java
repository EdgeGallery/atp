package org.edgegallery.atp.constant;

import java.io.File;
import org.edgegallery.atp.utils.file.FileChecker;

public interface Constant {

    String EMPTY = "";

    String TASK_ID = "taskId";

    String UNDER_LINE = "_";

    int MAX_TASK_THREAD_NUM = 10;

    String APP_NAME = "app_name";

    String APP_VERSION = "app_archive_version";

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

    interface Status {
        String SUCCESS = "success";

        String RUNNING = "running";

        String FAILED = "failed";

        String WAITING = "waiting";
    }

    interface FileOperation {
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
    }

    interface testCaseType {
        String VIRUS_SCAN_TEST = "virusScanningTest";

        String COMPLIANCE_TEST = "complianceTest";

        String SANDBOX_TEST = "sandboxTest";

    }

    interface URL {
        String INVENTORY_GET_MECHOSTS_URL = "/inventory/v1/tenants/tenantId/mechosts";

        String APP_STORE_GET_APP_PACKAGE = "/mec/appstore/v1/apps/%s/packages/%s";

        String APP_STORE_DOWNLOAD_CSAR = "/mec/appstore/v1/apps/%s/packages/%s/action/download";

        String APM_UPLOAD_PACKAGE = "/apm/v1/tenants/{tenant_id}/packages/upload";

        String APPO_CREATE_APPINSTANCE = "/appo/v1/tenants/{tenant_id}/app_instances";

        String APPO_DELETE_APPLICATION_INSTANCE = "/appo/v1/tenants/{tenant_id}/app_instances/{app_instance_id}";
    }

    interface DependencyAnalysis {
        String NODE_TEMPLATES = "node_templates";

        String APP_CONFIGURATION = "app_configuration";

        String PROPERTIES = "properties";

        String APP_SERVICE_REQUIRED = "appServiceRequired";

        String SERNAME = "serName";
    }

}
