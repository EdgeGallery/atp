/*
 * Copyright 2021 Huawei Technologies Co., Ltd.
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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Map;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * main yaml file existence validation.
 */
public class YamlDescriptionFileValidation {
    private static final String YAML_FILE_NOT_EXISTS
        = "there is no yaml file according to the definition in .meta file Entry-Definitions field.";

    private static final String ENTRY_DEFINITIONS_NOT_EXISTS = "there is no Entry-Definitions field in .meta file.";

    private static final String INNER_EXCEPTION = "inner exception, please check the log.";

    private static final String SUCCESS = "success";

    private static final int BUFFER = 1024;

    /**
     * execute test case.
     *
     * @param filePath csar file path
     * @param context context
     * @return result
     */
    public String execute(String filePath, Map<String, String> context) {
        delay();
        try (ZipFile zipFile = new ZipFile(filePath)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                // APPD/Definition/MainServiceTemplate.yaml
                String[] nameArray = entry.getName().split("/");
                // find zip package in APPD file path.
                if (nameArray.length == 2 && "APPD".equalsIgnoreCase(nameArray[0]) && nameArray[1].endsWith(".zip")) {
                    String yamlPath = getYamlPath(zipFile, entry);
                    return null != yamlPath ? analysizeAppdZip(zipFile, entry, yamlPath)
                        ? SUCCESS
                        : YAML_FILE_NOT_EXISTS : ENTRY_DEFINITIONS_NOT_EXISTS;
                }
            }
        } catch (IOException e) {
            return INNER_EXCEPTION;
        }

        return YAML_FILE_NOT_EXISTS;
    }

    /**
     * get main yaml file path.
     *
     * @param zipFile zipFile
     * @param entry entry
     * @return main yaml file path
     */
    private String getYamlPath(ZipFile zipFile, ZipEntry entry) {
        ZipEntry appdEntry;
        try (ZipInputStream appdZis = new ZipInputStream(zipFile.getInputStream(entry))) {
            while ((appdEntry = appdZis.getNextEntry()) != null) {
                // find .meta file and get main yaml file path
                if (appdEntry.getName().endsWith(".meta")) {
                    return analysizeMetaAndGetYamlPath(appdZis);
                }
            }
        } catch (IOException e) {
        }
        return null;
    }

    /**
     * analysize meta file and get yaml file path.
     *
     * @param appdZis appZis
     * @return yaml file path
     */
    private String analysizeMetaAndGetYamlPath(ZipInputStream appdZis) {
        byte[] data = getByte(appdZis);
        try (InputStream inputStream = new ByteArrayInputStream(data);
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line = "";
            while ((line = br.readLine()) != null) {
                // prefix: path
                String[] splitByColon = line.split(":");
                if (splitByColon.length > 1 && "Entry-Definitions".equalsIgnoreCase(splitByColon[0].trim())) {
                    return splitByColon[1].trim();
                }
            }
        } catch (IOException e) {
        }
        return null;
    }

    /**
     * analysize zip file and get main yaml file content.
     *
     * @param zipFile zipFile
     * @param entry entry
     * @param yamlPath yamlPath
     * @return result
     */
    private boolean analysizeAppdZip(ZipFile zipFile, ZipEntry entry, String yamlPath) {
        ZipEntry appdEntry;
        try (ZipInputStream appdZis = new ZipInputStream(zipFile.getInputStream(entry))) {
            while ((appdEntry = appdZis.getNextEntry()) != null) {
                if (yamlPath.equalsIgnoreCase(appdEntry.getName())) {
                    return true;
                }
            }
        } catch (IOException e) {
        }
        return false;
    }

    /**
     * get bytes from inputStream.
     *
     * @param zis inputStream
     * @return file bytes
     */
    public byte[] getByte(InflaterInputStream zis) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[BUFFER];
            int count = 0;
            while ((count = zis.read(buffer, 0, BUFFER)) != -1) {
                outputStream.write(buffer, 0, count);
            }
            return outputStream.toByteArray();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * delay some time.
     */
    private void delay() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e1) {
        }
    }
}
