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
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * unzip file must no more than 10G.
 */
public class UnzipFileSizeValidation {
    private static final int BUFFER = 512;

    private static final String FILE_TOO_BIG = "unzip file size must less than 10G";

    private static final String UNZIP_PACKAGE_ERROR = "unzip csar with exception";

    String WORK_TEMP_DIR = getDir() + File.separator + "temp/fileNumber/";

    public String execute(String filePath, Map<String, String> context) {
        ZipEntry entry;
        long total = 0;
        byte[] data = new byte[BUFFER];
        String tempDir = WORK_TEMP_DIR.concat(UUID.randomUUID().toString());

        try (FileInputStream fis = new FileInputStream(filePath);
                ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis))) {
            while ((entry = zis.getNextEntry()) != null) {
                int count;
                String name = sanitizeFileName(entry.getName(), tempDir);
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
            deleteFileDir(new File(tempDir));
        }

        return "success";
    }

    /**
     * delete file die.
     * 
     * @param file file
     */
    private void deleteFileDir(File file) {
        File[] files = file.listFiles();
        for (File eachFile : files) {
            if (eachFile.isDirectory()) {
                deleteFileDir(eachFile);
            } else {
                eachFile.delete();
            }
        }
        file.delete();
    }
    
    /**
     * get root dir.
     * 
     * @return root dir
     */
    private String getDir() {
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            return "C:\\atp";
        } else {
            return "/usr/atp";
        }
    }

    /**
     * get right file name.
     * 
     * @param entryName entryName
     * @param intendedDir intendedDir
     * @return file path
     * @throws IOException IOException
     */
    private String sanitizeFileName(String entryName, String intendedDir) throws IOException {
        File f = new File(intendedDir, entryName);
        String canonicalPath = f.getCanonicalPath();
        createFile(canonicalPath);
        return canonicalPath;
    }

    /**
     * create file.
     * 
     * @param filePath filePath
     * @throws IOException IOException
     */
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

    /**
     * judge file is dir.
     * 
     * @param entry entry
     * @param f file
     * @return is dir
     */
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
