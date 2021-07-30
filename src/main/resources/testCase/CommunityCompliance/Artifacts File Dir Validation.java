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

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * root path must contain Artifacts file dir.
 */
public class ArtifactsValidation {
    private static final String ARTIFACTS_NOT_EXISTS = "root path must contain Artifacts file dir.";

    private static final String INNER_EXCEPTION = "inner exception, please check the log.";

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
                if (entry.getName().startsWith("Artifacts/")) {
                    return "success";
                }
            }
        } catch (IOException e) {
            return INNER_EXCEPTION;
        }

        return ARTIFACTS_NOT_EXISTS;
    }

    /**
     * add delay.
     */
    private void delay() {
        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
        }
    }
}
