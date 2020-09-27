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

    String TENANT_ID = "tenantId";

    String IP = "ip";

    String PORT = "port";

    String DOT = ".";

    String COLON = ":";

    String APP_INSTANCE_ID = "appInstanceId";

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

        String COMPLIANCE_TEST = "compliancesTest";

        String SANDBOX_TEST = "sandboxTest";

    }

    interface URL {
        /**
         * /lcmcontroller/v1/tenants/{tenantId}/app_instances/{appInstanceId}/instantiate tenantId replaced
         * by appInstanceId is for variables replacement
         */
        String APP_LCM_INSTANTIATE_APP_URL =
                "/lcmcontroller/v1/tenants/appInstanceId/app_instances/appInstanceId/instantiate";

        /**
         * /lcmcontroller/v1/tenants/{tenantId}/app_instances/{appInstanceId}/terminate
         */
        String APP_LCM_TERMINATE_APP_URL =
                "/lcmcontroller/v1/tenants/appInstanceId/app_instances/appInstanceId/terminate";

        String INVENTORY_GET_MECHOSTS_URL = "/inventory/v1/tenants/tenantId/mechosts";
    }

}
