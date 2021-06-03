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

public class CPUNumberDescriptionValidation {
    private static final String CPU_DESCRIPTION_NOT_EXISTS = "There is no cpu description filed: num_virtual_cpu";
    private static final String INNER_EXCEPTION = "inner exception, please check the log.";
    private static final String NUM_VIRTUAL_CPU = "num_virtual_cpu";
    private static final String SUCCESS = "success";

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
                String[] nameArray = entry.getName().split("/");
                // find zip package in APPD file path.
                if (nameArray.length == 2 && "APPD".equalsIgnoreCase(nameArray[0]) && nameArray[1].endsWith(".zip")) {
                    String yamlPath = getYamlPath(zipFile, entry);
                    if (null != yamlPath) {
                        return analysizeAppdZip(zipFile, entry, yamlPath) ? SUCCESS : CPU_DESCRIPTION_NOT_EXISTS;
                    }
                }
            }
        } catch (IOException e) {
            return INNER_EXCEPTION;
        }

        return INNER_EXCEPTION;
    }

    /**
     * delay some time.
     */
    private void delay() {
        try {
            Thread.sleep(600);
        } catch (InterruptedException e1) {
        }
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
                    byte[] data = getByte(appdZis);
                    InputStream inputStream = new ByteArrayInputStream(data);
                    try (BufferedReader br =
                            new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                        String line = "";
                        while ((line = br.readLine()) != null) {
                            // prefix: path
                          String[] splitByColon = line.split(":");
                          if (splitByColon.length > 1
                                  && "Entry-Definitions".equalsIgnoreCase(splitByColon[0].trim())) {
                              return splitByColon[1].trim();
                          }
                        }
                    }
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
                    // this is main yaml file.
                    byte[] data = getByte(appdZis);
                    InputStream inputStream = new ByteArrayInputStream(data);
                    return hasCPUDescription(inputStream);
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
            byte[] buffer = new byte[1024];
            int count = 0;
            while ((count = zis.read(buffer, 0, 1024)) != -1) {
                outputStream.write(buffer, 0, count);
            }
            return outputStream.toByteArray();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * analysize if has cpu description field/
     * 
     * @param inputStream main yaml file content
     * @return result
     */
    private boolean hasCPUDescription(InputStream inputStream) {
        try (BufferedReader br =
                new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line = "";
            while ((line = br.readLine()) != null) {
                if (line.trim().startsWith(NUM_VIRTUAL_CPU)) {
                    String[] splitByColon = line.split(":");
                    // Source: path
                    if (splitByColon.length > 1 && NUM_VIRTUAL_CPU.equalsIgnoreCase(splitByColon[0].trim())) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
        }

        return false;
    }

}
