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

package org.edgegallery.atp.utils.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.utils.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import com.google.common.io.Files;

public class FileChecker {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileChecker.class);

    /**
     * check if file path is valid.
     * 
     * @param filePath file path.
     * @return
     */
    public static String check(String filePath) {
        filePath = Normalizer.normalize(filePath, Normalizer.Form.NFKC);

        if (StringUtils.isEmpty(filePath)) {
            throw new IllegalArgumentException(filePath + " :filepath is empty");
        }

        // file name should not contains blank.
        if (filePath != null && filePath.split("\\s").length > 1) {
            throw new IllegalArgumentException(filePath + " :filepath contain blank");
        }

        String name = filePath.toLowerCase();
        if (!(name.endsWith(Constant.MANIFEST) || name.endsWith(Constant.MARK_DOWN)
                || name.endsWith(Constant.PACKAGE_XML_FORMAT) || name.endsWith(Constant.PACKAGE_YAML_FORMAT)
                || name.endsWith(Constant.PACKAGE_CSH_FORMAT) || name.endsWith(Constant.PACKAGE_META_FORMAT)
                || name.endsWith(Constant.PACKAGE_TXT_FORMAT))) {
            throw new IllegalArgumentException();
        }

        String[] dirs = filePath.split(":");
        for (String dir : dirs) {
            Matcher matcher = Pattern.compile(Constant.REG).matcher(dir);
            if (!matcher.matches()) {
                throw new IllegalArgumentException();
            }
        }
        return filePath.replace(":", File.separator);

    }

    /**
     * check file if is invalid.
     * 
     * @param file object.
     */
    public static File check(MultipartFile file, String taskId) {
        String originalFilename = file.getOriginalFilename();

        // file name should not contains blank.
        if (originalFilename != null && originalFilename.split("\\s").length > 1) {
            throw new IllegalArgumentException(originalFilename + " :fileName contain blank");
        }

        if (originalFilename != null && !isAllowedFileName(originalFilename)) {
            throw new IllegalArgumentException(originalFilename + " :fileName is Illegal");
        }

        if (file.getSize() > getMaxFileSize()) {
            throw new IllegalArgumentException(originalFilename + " :fileSize is too big");
        }

        File result = null;
        String originalFileName = file.getOriginalFilename();

        if (originalFileName == null) {
            throw new IllegalArgumentException("Package File name is null.");
        }

        // temp/taskId_fileName
        String tempFileAddress = new StringBuilder().append(FileChecker.getDir()).append(File.separator).append("temp")
                .append(File.separator).append(taskId).append(Constant.UNDER_LINE).append(file.getOriginalFilename())
                .toString();
        try {
            createFile(tempFileAddress);
            result = new File(tempFileAddress);
            file.transferTo(result);
            unzip(tempFileAddress);
        } catch (IOException e) {
            CommonUtil.deleteTempFile(taskId, file);
            throw new IllegalArgumentException("create temp file with IOException");
        } catch (IllegalStateException e) {
            CommonUtil.deleteTempFile(taskId, file);
            throw new IllegalArgumentException(e.getMessage());
        }
        return result;
    }

    /**
     * get directory of different system
     * 
     * @return
     */
    public static String getDir() {
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            return "C:\\atp";
        } else {
            return "/usr/atp";
        }
    }

    /**
     * dependency application check
     * 
     * @param filePath
     * @return dependency application info list, contains appName,appId and appPackageId.
     */
    public static List<Map<String, String>> dependencyCheck(String filePath) {
        List<Map<String, String>> result = new ArrayList<Map<String, String>>();
        try (ZipFile zipFile = new ZipFile(filePath)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String[] pathSplit = entry.getName().split(Constant.SLASH);

                // fileName/APPD/Definition/MainServiceTemplate.yaml
                if (pathSplit.length == 4 && Constant.DEFINITIONS.equals(pathSplit[2].trim())
                        && pathSplit[3].trim().endsWith(Constant.PACKAGE_YAML_FORMAT)) {
                    analysisDependency(result, zipFile, entry);
                }
            }
        } catch (IOException e) {
            LOGGER.error("dependency Check failed. {}", e.getMessage());
        }
        return result;
    }

    private static void analysisDependency(List<Map<String, String>> result, ZipFile zipFile, ZipEntry entry) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(zipFile.getInputStream(entry)))) {
            String line = positionDependencyService(br);
            if (StringUtils.isEmpty(line)) {
                LOGGER.error(
                        "can not find the dependency path, the dependency path must be in node_templates.app_configuration.properties.appServiceRequired");
                throw new IllegalArgumentException(
                        "can not find the dependency path, the dependency path must be in node_templates.app_configuration.properties.appServiceRequired");
            }
            // -serName
            while (line != null && line.trim().startsWith(Constant.STRIKE)) {
                Map<String, String> map = new HashMap<String, String>();
                map.put(Constant.APP_NAME, line.split(Constant.COLON)[1].trim());
                while ((line = br.readLine()) != null && !isEnd(line) && !line.trim().startsWith(Constant.STRIKE)) {
                    line = line.trim();
                    if (line.startsWith(Constant.APP_ID)) {
                        map.put(Constant.APP_ID, line.split(Constant.COLON)[1].trim());
                    } else if (line.startsWith(Constant.PACKAGE_ID)) {
                        map.put(Constant.PACKAGE_ID, line.split(Constant.COLON)[1].trim());
                    }
                }
                result.add(map);
            }

        } catch (IOException e) {
            LOGGER.error("analysis dependency failed. {}", e.getMessage());
        }
    }

    /**
     * if depedency service define end.
     * 
     * @param str yaml line
     * @return is depedency service define end.
     */
    private static boolean isEnd(String str) {
        return str.split(Constant.COLON).length <= 1;
    }

    /**
     * position to dependency service field
     * 
     * @param br bufferReader
     * @return line
     * @throws IOException
     */
    private static String positionDependencyService(BufferedReader br) throws IOException {
        String line = "";
        while ((line = br.readLine()) != null) {
            if (line.trim().startsWith(Constant.NODE_TEMPLATES)) {
                while ((line = br.readLine()) != null) {
                    if (line.trim().startsWith(Constant.APP_CONFIGURATION)) {
                        while ((line = br.readLine()) != null) {
                            if (line.trim().startsWith(Constant.PROPERTIES)) {
                                while ((line = br.readLine()) != null) {
                                    if (line.trim().startsWith(Constant.APP_SERVICE_REQUIRED)) {
                                        return br.readLine().trim();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return line;
    }

    /**
     * Prevent bomb attacks.
     *
     * @param fileName file name.
     * @throws java.io.IOException throw IOException
     */
    private static void unzip(String fileName) throws IOException {
        FileInputStream fis = FileUtils.openInputStream(new File(fileName));
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
        ZipEntry entry;
        int entries = 0;
        int total = 0;
        byte[] data = new byte[Constant.BUFFER];
        String name = "";
        try {
            while ((entry = zis.getNextEntry()) != null) {
                int count;
                // Write the files to the disk, but ensure that the entryName is valid,
                // and that the file is not insanely big
                name = sanitzeFileName(entry.getName(), Constant.WORK_TEMP_DIR);
                File f = new File(name);
                if (isDir(entry, f)) {
                    continue;
                }
                FileOutputStream fos = FileUtils.openOutputStream(f);
                try (BufferedOutputStream dest = new BufferedOutputStream(fos, Constant.BUFFER)) {
                    while (total <= Constant.TOO_BIG && (count = zis.read(data, 0, Constant.BUFFER)) != -1) {
                        dest.write(data, 0, count);
                        total += count;
                    }
                    dest.flush();
                }
                zis.closeEntry();
                entries++;
                if (entries > Constant.TOO_MANY) {
                    throw new IllegalStateException("Too many files to unzip.");
                }
                if (total > Constant.TOO_BIG) {
                    throw new IllegalStateException("File being unzipped is too big.");
                }
            }
        } catch (IOException e) {
            FileUtils.cleanDirectory(new File(Constant.WORK_TEMP_DIR));
            throw new IllegalArgumentException("unzip csar with exception.");
        } finally {
            zis.close();
            if (!StringUtils.isEmpty(name)) {
                new File(name).delete();
            }
        }
    }

    private static boolean isAllowedFileName(String originalFilename) {
        return isValid(originalFilename)
                && getFileExtensions().contains(Files.getFileExtension(originalFilename.toLowerCase()));
    }


    /**
     * check if file name if it's invalid.
     * 
     * @param fileName file name
     * @return
     */
    private static boolean isValid(String fileName) {
        if (StringUtils.isEmpty(fileName) || fileName.length() > Constant.MAX_LENGTH_FILE_NAME) {
            return false;
        }
        fileName = Normalizer.normalize(fileName, Normalizer.Form.NFKC);
        Matcher matcher = Pattern.compile(Constant.REG).matcher(fileName);
        if (!matcher.matches()) {
            return false;
        }
        return true;
    }


    private static void createFile(String filePath) throws IOException {
        File tempFile = new File(filePath);
        boolean result = false;

        if (!tempFile.getParentFile().exists() && !tempFile.isDirectory()) {
            result = tempFile.getParentFile().mkdirs();
        }
        if (!tempFile.exists() && !tempFile.isDirectory() && !tempFile.createNewFile() && !result) {
            throw new IllegalArgumentException("create temp file failed");
        }
    }

    private static long getMaxFileSize() {
        // 50MB
        return 50 * 1024 * 1024;
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

    private static String sanitzeFileName(String entryName, String intendedDir) throws IOException {

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

}


