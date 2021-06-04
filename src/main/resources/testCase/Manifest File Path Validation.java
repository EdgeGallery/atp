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

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Implementation of validating .mf file must be in root directory.
 */
public class SuffixTestCaseInner {
    private static final String INNER_EXCEPTION = "inner exception, please check the log.";
    private static final String FILE_NOT_EXIST = ".mf file may not exist or it do not in the root directory.";
    private static final String MF_FILE_NUMBER_TOO_MUCH = "there can be only one mf file in the root directory.";

    /**
     * execute test case.
     * 
     * @param filePath csar file path
     * @param context context
     * @return result
     */
    public String execute(String filePath, Map<String, String> context) {
        int num = 0;
        try (ZipFile zipFile = new ZipFile(filePath)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                // root directory and file is end of mf
                if (entry.getName().split("/").length == 1 && fileSuffixValidate("mf", entry.getName())) {
                    num++;
                    return "success";
                }
            }
        } catch (IOException e) {
            return INNER_EXCEPTION;
        }

        switch (num) {
            case 0:
                return FILE_NOT_EXIST;
            case 1:
                return "success";
            default:
                return MF_FILE_NUMBER_TOO_MUCH;
        }
    }

    /**
     * if file suffix is in pattern.
     * 
     * @param pattern pattern
     * @param fileName fileName
     * @return file suffix is in pattern
     */
    public static boolean fileSuffixValidate(String pattern, String fileName) {
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        if (null != suffix && "" != suffix && suffix.equals(pattern)) {
            return true;
        }
        return false;
    }
}
