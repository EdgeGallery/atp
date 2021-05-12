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

public class SourcePathTestCaseInner {

    private static final String SLASH = "/";

    private static final String INNER_EXCEPTION = "inner exception, please check the log.";

    private static final String SOURCE_PATH_FILE_NOT_EXISTS = "some source path file in .mf may not exist.";

    private static Set<String> pathSet = new HashSet<String>();

    private static final String SUCCESS = "success";

    public String execute(String filePath, Map<String, String> context) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e1) {
        }
        Set<String> sourcePathSet = new HashSet<String>();
        try (ZipFile zipFile = new ZipFile(filePath)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                pathSet.add(removeLastSlash(entry.getName()));

                // root directory and file is end of mf
                if (entry.getName().split(SLASH).length == 1
                        && fileSuffixValidate("mf", entry.getName())) {
                    Set<String> prefix = new HashSet<String>() {
                        {
                            add("Source");
                        }
                    };
                    sourcePathSet = getPathSet(zipFile, entry, prefix);
                }
            }
        } catch (IOException e) {
            return INNER_EXCEPTION;
        }
        return pathSet.containsAll(sourcePathSet) ? SUCCESS : SOURCE_PATH_FILE_NOT_EXISTS;
    }

    private boolean fileSuffixValidate(String pattern, String fileName) {
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        if ((null != suffix) && suffix.equals(pattern)) {
            return true;
        }
        return false;
    }

    private Set<String> getPathSet(ZipFile zipFile, ZipEntry entry, Set<String> prefixSet) {
        Set<String> pathSet = new HashSet<String>();
        try (BufferedReader br =
                new BufferedReader(new InputStreamReader(zipFile.getInputStream(entry), StandardCharsets.UTF_8))) {
            String line = "";
            while ((line = br.readLine()) != null) {
                String[] splitByColon = line.split(":");
                // prefix: path
                if (splitByColon.length > 1 && prefixSet.contains(splitByColon[0])) {
                    pathSet.add(splitByColon[1].trim());
                }
            }
        } catch (IOException e) {
        }

        return pathSet;
    }

    private String removeLastSlash(String path) {
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    /**
     * get app_type
     * 
     * @param filePath filePath
     * @return appType
     */
    private String getAppType(String filePath) {
        try (ZipFile zipFile = new ZipFile(filePath)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.getName().split("/").length == 1 && entry.getName().endsWith(".mf")) {
                    try (BufferedReader br = new BufferedReader(
                            new InputStreamReader(zipFile.getInputStream(entry), StandardCharsets.UTF_8))) {
                        String line = "";
                        while ((line = br.readLine()) != null) {
                            // prefix: path
                            if (line.trim().startsWith("app_class")) {
                                return line.split(":")[1].trim();
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
        }
        return null;
    }
}
