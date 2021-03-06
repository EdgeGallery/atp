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

import java.io.File;
import java.util.Map;

/**
 * package file size must less than 5GB.
 *
 */
public class FileSizeValidation {
    private static final long MAX_SIZE = 5 * 1024 * 1024 * 1024;
    private static final String FILE_TOO_BIG = "package file size must less than 5GB.";

    /**
     * execute test case.
     * 
     * @param filePath csar file path
     * @param context context
     * @return result
     */
    public String execute(String filePath, Map<String, String> context) {
        try {
            Thread.sleep(800);
        } catch (InterruptedException e1) {
        }

        File file = new File(filePath);
        if (file.length() <= MAX_SIZE) {
            return "success";
        } else {
            return FILE_TOO_BIG;
        }
    }
}
