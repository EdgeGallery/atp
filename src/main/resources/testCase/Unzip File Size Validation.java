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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnzipFileSizeValidation {
    private static final int BUFFER = 512;

    private static final String FILE_TOO_BIG = "unzip file size must less than 10G";

    private static final String UNZIP_PACKAGE_ERROR = "unzip csar with exception";

    String WORK_TEMP_DIR = getDir() + File.separator + "temp/fileNumber";

    public String execute(String filePath, Map<String, String> context) {
        try {
            Thread.sleep(800);
        } catch (InterruptedException e1) {
        }
        
        ZipEntry entry;
        long total = 0;
        byte[] data = new byte[BUFFER];

        try (FileInputStream fis = new FileInputStream(filePath);
                ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis))) {
            while ((entry = zis.getNextEntry()) != null) {
                int count;
                String name = sanitzeFileName(entry.getName(), WORK_TEMP_DIR);
                File f = new File(name);
                if (isDir(entry, f)) {
                    continue;
                }

                FileOutputStream fos = new FileOutputStream(name);
                try (BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER)) {
                    while (total <= 0x280000000L && (count = zis.read(data, 0, BUFFER)) != -1) {
                        dest.write(data, 0, count);
                        total += count;
                    }
                    dest.flush();
                }
                zis.closeEntry();
                if (total > 0x280000000L) {
                    return FILE_TOO_BIG;
                }
            }
        } catch (IOException e) {
            return UNZIP_PACKAGE_ERROR;
        } finally {
            new File(WORK_TEMP_DIR).delete();
        }

        return "success";
    }

    private String getDir() {
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            return "C:\\atp";
        } else {
            return "/usr/atp";
        }
    }

    private String sanitzeFileName(String entryName, String intendedDir) throws IOException {
        File f = new File(intendedDir, entryName);
        String canonicalPath = f.getCanonicalPath();
        createFile(canonicalPath);
        return canonicalPath;
    }

    private void createFile(String filePath) throws IOException {
        File tempFile = new File(filePath);
        boolean result = false;

        if (!tempFile.getParentFile().exists() && !tempFile.isDirectory()) {
            result = tempFile.getParentFile().mkdirs();
        }
        if (!tempFile.exists() && !tempFile.isDirectory() && !tempFile.createNewFile() && !result) {
            throw new IllegalArgumentException("create temp file failed");
        }
    }

    private boolean isDir(ZipEntry entry, File f) {
        if (entry.isDirectory()) {
            boolean isSuccess = f.mkdirs();
            if (isSuccess) {
                return true;
            } else {
                return f.exists();
            }
        }
        return false;
    }
}
