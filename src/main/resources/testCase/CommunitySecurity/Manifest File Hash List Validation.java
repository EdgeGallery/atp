/*
 * Copyright 2020 Huawei Technologies Co., Ltd.
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
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Each Source file must has hash description.
 *
 */
public class ManifestFileHashListValidation {
    private static final String HASH_FIELD_NOT_EXISTS = "Some Source file does not have Hash filed.";
    private static final String INNER_EXCEPTION = "inner exception, please check the log.";
    private static final String SOURCE = "Source";

    /**
     * execute test case.
     * 
     * @param filePath csar file path
     * @param context context
     * @return result
     */
    public String execute(String filePath, Map<String, String> context) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e1) {
        }
        try (ZipFile zipFile = new ZipFile(filePath)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.getName().split("/").length == 1 && fileSuffixValidate("mf", entry.getName())) {
                    return hasHash(zipFile, entry) ? "success" : HASH_FIELD_NOT_EXISTS;
                }
            }
        } catch (IOException e) {
            return INNER_EXCEPTION;
        }

        return INNER_EXCEPTION;
    }

    /**
     * if has hash description.
     * 
     * @param zipFile zipFile
     * @param entry entry
     * @return has hash description
     */
    private boolean hasHash(ZipFile zipFile, ZipEntry entry) {
        try (BufferedReader br =
                new BufferedReader(new InputStreamReader(zipFile.getInputStream(entry), StandardCharsets.UTF_8))) {
            String line = "";
            while ((line = br.readLine()) != null) {
                String[] splitByColon = line.split(":");
                // Source: path
                if (splitByColon.length > 1 && SOURCE.equals((splitByColon[0]))) {
                    boolean flag = true;
                    while ((line = br.readLine()) != null) {
                        String[] array = line.split(":");
                        if (SOURCE.equals((array[0]))) {
                            if (flag) {
                                return false;
                            } else {
                                flag = true;
                            }
                        }
                        if ("Hash".equals((array[0]))) {
                            flag = false;
                        }
                    }
                }
            }
        } catch (IOException e) {
        }

        return true;
    }

    /**
     * if file suffix is in pattern.
     * 
     * @param pattern pattern
     * @param fileName fileName
     * @return file suffix is in pattern
     */
    private boolean fileSuffixValidate(String pattern, String fileName) {
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        if ((null != suffix) && suffix.equals(pattern)) {
            return true;
        }
        return false;
    }
}
