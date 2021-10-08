/*
 * Copyright 2020-2021 Huawei Technologies Co., Ltd.
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

package org.edgegallery.atp.utils;

import com.google.common.io.Files;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.constant.ErrorCode;
import org.edgegallery.atp.utils.exception.IllegalRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

public class FileChecker {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileChecker.class);

    private FileChecker() {

    }

    /**
     * check file if is invalid.
     *
     * @param file object.
     */
    public static File check(MultipartFile file, String taskId) {
        fileNameCheck(file.getOriginalFilename());

        if (file.getSize() > getMaxFileSize()) {
            LOGGER.error("fileSize is too big");
            throw new IllegalRequestException(String.format(ErrorCode.SIZE_OUT_OF_LIMIT_MSG, "file", "5G"),
                ErrorCode.SIZE_OUT_OF_LIMIT, new ArrayList<String>(Arrays.asList("file", "5G")));
        }

        File result = null;
        // temp/taskId_fileName
        String tempFileAddress = new StringBuilder().append(Constant.WORK_TEMP_DIR).append(File.separator)
            .append(taskId).append(Constant.UNDER_LINE).append(file.getOriginalFilename()).toString();
        try {
            createFile(tempFileAddress);
            try (FileOutputStream fos = new FileOutputStream(tempFileAddress)) {
                byte[] bytes = file.getBytes();
                fos.write(bytes);
            }
            result = new File(tempFileAddress);
            unzip(tempFileAddress);
        } catch (IOException e) {
            if (!CommonUtil.deleteTempFile(taskId, file)) {
                LOGGER.warn("check delete file {} failed.", file.getOriginalFilename());
            }
            LOGGER.error("create temp file with IOException. {}", e.getMessage());
            throw new IllegalRequestException(ErrorCode.FILE_IO_EXCEPTION_MSG, ErrorCode.FILE_IO_EXCEPTION, null);
        }

        return result;
    }

    /**
     * copy file to target path.
     *
     * @param file source file
     * @param path target path
     */
    public static void copyMultiFileToDir(MultipartFile file, String path) {
        try {
            createFile(path);
            File targetFile = new File(path);
            file.transferTo(targetFile);
        } catch (IOException e) {
            LOGGER.error("copy file to dir with IOException,{}", e.getMessage());
            throw new IllegalArgumentException("copy file to dir with IOException");
        }
    }

    /**
     * get directory of different system.
     *
     * @return path
     */
    public static String getDir() {
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            return "C:\\atp";
        } else {
            return "/usr/atp";
        }
    }

    /**
     * Prevent bomb attacks.
     *
     * @param fileName file name.
     * @throws java.io.IOException throw IOException
     */
    public static void unzip(String fileName) throws IOException {
        ZipEntry entry;
        int entries = 0;
        long total = 0;
        byte[] data = new byte[Constant.BUFFER];
        String tempDir = Constant.WORK_TEMP_DIR + File.separator + CommonUtil.generateId();

        try (FileInputStream fis = FileUtils.openInputStream(new File(fileName));
             ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));) {
            while ((entry = zis.getNextEntry()) != null) {
                int count;
                // Write the files to the disk, but ensure that the entryName is valid,
                // and that the file is not insanely big
                String name = sanitizeFileName(entry.getName(), tempDir);
                File f = new File(name);
                if (isDir(entry, f)) {
                    continue;
                }

                try (FileOutputStream fos = FileUtils.openOutputStream(f);
                     BufferedOutputStream dest = new BufferedOutputStream(fos, Constant.BUFFER)) {
                    while (total <= Constant.TOO_BIG && (count = zis.read(data, 0, Constant.BUFFER)) != -1) {
                        dest.write(data, 0, count);
                        total += count;
                    }
                    dest.flush();
                }
                entries++;
                if (entries > Constant.TOO_MANY) {
                    LOGGER.error("Too many files to unzip.");
                    throw new IllegalRequestException(
                        String.format(ErrorCode.NUMBER_OUT_OF_LIMIT_MSG, "unzip files", "1024"),
                        ErrorCode.NUMBER_OUT_OF_LIMIT, new ArrayList<String>(Arrays.asList("unzip files", "1024")));
                }
                if (total > Constant.TOO_BIG) {
                    LOGGER.error("File being unzipped is too big.");
                    throw new IllegalRequestException(
                        String.format(ErrorCode.SIZE_OUT_OF_LIMIT_MSG, "unzip file", "10G"),
                        ErrorCode.SIZE_OUT_OF_LIMIT, new ArrayList<String>(Arrays.asList("unzip file", "10G")));
                }
            }
        } catch (IOException e) {
            LOGGER.error("unzip csar with exception. {}", e.getMessage());
            throw new IllegalRequestException(ErrorCode.FILE_IO_EXCEPTION_MSG, ErrorCode.FILE_IO_EXCEPTION, null);
        } finally {
            FileUtils.cleanDirectory(new File(tempDir));
            CommonUtil.deleteFile(tempDir);
        }
    }

    private static boolean isAllowedFileName(String originalFilename) {
        return isValid(originalFilename) && getFileExtensions()
            .contains(Files.getFileExtension(originalFilename.toLowerCase()));
    }

    /**
     * check if file name if it's invalid.
     *
     * @param fileName file name
     * @return
     */
    public static boolean isValid(String fileName) {
        if (StringUtils.isEmpty(fileName) || fileName.length() > Constant.MAX_LENGTH_FILE_NAME) {
            return false;
        }
        fileName = Normalizer.normalize(fileName, Normalizer.Form.NFKC);
        Matcher matcher = Pattern.compile(Constant.REG).matcher(fileName);
        return matcher.matches();
    }

    /**
     * create file.
     *
     * @param filePath filePath
     * @throws IOException IOException
     */
    public static void createFile(String filePath) throws IOException {
        File tempFile = new File(filePath);
        boolean result = false;

        if (!tempFile.getParentFile().exists() && !tempFile.isDirectory()) {
            result = tempFile.getParentFile().mkdirs();
        }
        if (!tempFile.exists() && !tempFile.isDirectory() && !tempFile.createNewFile() && !result) {
            LOGGER.error("create temp file failed.");
            throw new IllegalRequestException(ErrorCode.FILE_IO_EXCEPTION_MSG, ErrorCode.FILE_IO_EXCEPTION, null);
        }
    }

    private static long getMaxFileSize() {
        // 5G
        return 5 * 1024 * 1024 * 1024;

    }

    private static List<String> getFileExtensions() {
        return Collections.singletonList("csar");
    }

    /**
     * check if entry is directory, if then create dir.
     *
     * @param entry entry of next element.
     * @param f File
     * @return
     */
    private static boolean isDir(ZipEntry entry, File f) {
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

    private static String sanitizeFileName(String entryName, String intendedDir) throws IOException {
        File f = new File(intendedDir, entryName);
        String canonicalPath = f.getCanonicalPath();
        File intendDir = new File(intendedDir);
        if (intendDir.isDirectory() && !intendDir.exists()) {
            createFile(intendedDir);
        }
        String canonicalID = intendDir.getCanonicalPath();
        if (canonicalPath.startsWith(canonicalID)) {
            return canonicalPath;
        } else {
            throw new IllegalStateException("File is outside extraction target directory.");
        }
    }

    /**
     * file name check.
     *
     * @param originalFileName originalFileName
     */
    public static void fileNameCheck(String originalFileName) {
        if (originalFileName == null) {
            LOGGER.error("Package File name is null.");
            throw new IllegalRequestException(String.format(ErrorCode.PARAM_IS_NULL_MSG, "package file name"),
                ErrorCode.PARAM_IS_NULL, new ArrayList<String>(Arrays.asList("package file name")));
        }

        // file name should not contains blank.
        if (originalFileName.split("\\s").length > 1) {
            LOGGER.error("fileName contain blank");
            throw new IllegalRequestException(ErrorCode.FILE_NAME_CONTAIN_BLANK_MSG, ErrorCode.FILE_NAME_CONTAIN_BLANK,
                null);
        }

        if (!isAllowedFileName(originalFileName)) {
            LOGGER.error("fileName is Illegal");
            throw new IllegalRequestException(ErrorCode.FILE_NAME_ILLEGAL_MSG, ErrorCode.FILE_NAME_ILLEGAL, null);
        }
    }

}


