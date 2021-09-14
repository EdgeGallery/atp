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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * mf file requirement field validation.
 */
public class MFContentTestCaseInner {
    private static final String MF_LOSS_FIELD
        = ".mf file may lost the following fileds:app_product_name,app_provider_id,app_package_version,app_class,app_release_date_time or app_package_description.";

    private static final String INNER_EXCEPTION = "inner exception, please check the log.";

    private static final String FILE_NOT_EXIST = ".mf file may not exist or it do not in the root directory.";

    private static Set<String> field = new HashSet<String>() {
        {
            add("app_product_name");
            add("app_provider_id");
            add("app_package_version");
            add("app_release_data_time");
            add("app_package_description");
            add("app_class");
        }
    };

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
                if (entry.getName().split("/").length == 1 && fileSuffixValidate("mf", entry.getName())) {
                    // some fields not exist in tosca.meta file
                    return isExistAll(zipFile, entry, field) ? "success" : MF_LOSS_FIELD;
                }
            }
        } catch (IOException e) {
            return INNER_EXCEPTION;
        }

        return FILE_NOT_EXIST;
    }

    /**
     * file suffix is in pattern.
     *
     * @param pattern pattern
     * @param fileName fileName
     * @return file suffix is in pattern.
     */
    private boolean fileSuffixValidate(String pattern, String fileName) {
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        return null != suffix && suffix.equals(pattern);
    }

    /**
     * if contain all field.
     *
     * @param zipFile zipFile
     * @param entry entry
     * @param prefixSet required fields set
     * @return contain all field
     */
    private boolean isExistAll(ZipFile zipFile, ZipEntry entry, Set<String> prefixSet) {
        Set<String> sourcePathSet = new HashSet<String>();
        try (BufferedReader br = new BufferedReader(
            new InputStreamReader(zipFile.getInputStream(entry), StandardCharsets.UTF_8))) {
            String line = "";
            while ((line = br.readLine()) != null) {
                // prefix: path
                String[] splitByColon = line.split(":");
                if (splitByColon.length > 1 && prefixSet.contains(splitByColon[0].trim())) {
                    sourcePathSet.add(splitByColon[0].trim());
                }
            }
        } catch (IOException e) {
        }

        return sourcePathSet.containsAll(prefixSet);
    }

    /**
     * add delay.
     */
    private void delay() {
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
        }
    }
}
