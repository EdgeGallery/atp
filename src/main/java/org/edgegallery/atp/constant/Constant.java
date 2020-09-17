package org.edgegallery.atp.constant;

import java.io.File;

import org.edgegallery.atp.utils.file.FileChecker;

public interface Constant {
    interface Result {
        String SUCCESS = "success";

        String RUNNING = "running";

        String FAILED = "failed";
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

    String EMPTY = "";

    String TASK_ID = "taskId";
}
