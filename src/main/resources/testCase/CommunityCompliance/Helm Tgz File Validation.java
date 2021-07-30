package com.example.demo.compliance;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * existence of helm tgz file validation
 */
public class HelmTgzFileValidation {
    private static final String INNER_EXCEPTION = "inner exception, please check the log.";

    private static final String TGZ_NOT_EXISTS = "there is no .tgz file in Artifacts/Deployment/Charts dir";

    /**
     * execute test case.
     *
     * @param filePath csar file path
     * @param context context info
     * @return execute result
     */
    public String execute(String filePath, Map<String, String> context) {
        delay();
        try (ZipFile zipFile = new ZipFile(filePath)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.getName().startsWith("Artifacts/Deployment/Charts") && entry.getName().endsWith(".tgz")) {
                    return "success";
                }
            }
        } catch (IOException e) {
            return INNER_EXCEPTION;
        }

        return TGZ_NOT_EXISTS;
    }

    /**
     * add delay.
     */
    private void delay() {
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
        }
    }
}
