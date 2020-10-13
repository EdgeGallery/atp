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

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.BoundedInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.Gson;


public class BasicInfo {

    private static final Logger LOGGER = LoggerFactory.getLogger(BasicInfo.class);

    private static final int BUFFER_SIZE = 2 * 1024 * 1024;

    public static final String PACKAGE_XML_FORMAT = ".xml";

    public static final String PACKAGE_YAML_FORMAT = ".yaml";

    public static final String MANIFEST = ".mf";

    public static final String MARKDOWN = ".md";

    public static final String MF_VERSION_META = "app_archive_version";

    public static final String MF_PRODUCT_NAME = "app_name";

    public static final String MF_PROVIDER_META = "app_provider";

    public static final String MF_APP_CONTACT = "app_contact";

    public static final String CSAR_EXTENSION = ".csar";

    private static final int LINE_MAX_LEN = 4096;

    private String appName;

    private String provider;

    private String contact;

    private String version;

    private String fileType;

    private String fileStructure;

    private String markDownContent;

    public String getFileType() {
        return fileType;
    }

    public String getMarkDownContent() {
        return markDownContent;
    }

    public static String getUnzipDir(String dirName) {
        File tmpDir = new File(File.separator + dirName);
        return tmpDir.getAbsolutePath().replace(CSAR_EXTENSION, "");
    }

    /**
     * create dir.
     *
     * @param dir dir to create
     * @return boolean
     */
    public static boolean createDirectory(String dir) {
        File folder = new File(dir);
        return folder.exists() || folder.mkdirs();
    }

    /**
     * unzip zip file.
     *
     * @param zipFileName file name to zip
     * @param extPlace extPlace
     * @return unzip file name
     * @throws IOException e1
     */
    public static List<String> unzip(String zipFileName, String extPlace) throws IOException {
        List<String> unzipFileNams = new ArrayList<String>();
        try (ZipFile zipFile = new ZipFile(zipFileName)) {
            Enumeration<?> fileEn = zipFile.entries();
            byte[] buffer = new byte[BUFFER_SIZE];
            while (fileEn.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) fileEn.nextElement();
                if (entry.isDirectory()) {
                    continue;
                }
                File file = new File(extPlace, entry.getName());
                if (!file.getParentFile().exists()) {
                    createDirectory(file.getParentFile().getAbsolutePath());
                }
                try (InputStream input = zipFile.getInputStream(entry);
                        FileOutputStream out = FileUtils.openOutputStream(file);
                        BufferedOutputStream bos = new BufferedOutputStream(out)) {
                    int length = 0;
                    while ((length = input.read(buffer)) != -1) {
                        bos.write(buffer, 0, length);
                    }
                    unzipFileNams.add(file.getAbsolutePath());
                }
            }
        }
        return unzipFileNams;
    }

    /**
     * judge the file's format is yaml or not.
     *
     * @param file file to judge
     * @return boolean
     */
    public static boolean isYamlFile(File file) {
        return !file.isDirectory() && file.getName().indexOf(".yaml") != -1;
    }

    /**
     * load file and analyse file list.
     *
     * @param fileAddress file storage path.
     * @return
     */
    public BasicInfo load(String fileAddress) {
        String unzipDir = getUnzipDir(fileAddress);
        boolean isXmlCsar = false;
        try {
            List<String> unzipFiles = unzip(fileAddress, unzipDir);
            if (unzipFiles.isEmpty()) {
                isXmlCsar = true;
            }
            FileRelationResponse treeStruct = buildFileStructure(unzipDir, unzipDir);
            Gson gson = new Gson();
            fileStructure = gson.toJson(treeStruct);
            for (String unzipFile : unzipFiles) {
                boolean isRegular = isRegularFile(Paths.get(unzipFile));
                if (!isRegular) {
                    break;
                }
                File f = new File(unzipFile);
                String canonicalPath = f.getCanonicalPath();
                if (canonicalPath.endsWith(MANIFEST)) {
                    readManifest(f);
                }
                if (canonicalPath.endsWith(MARKDOWN)) {
                    readMarkDown(f);
                }
                if (isYamlFile(f)) {
                    isXmlCsar = false;
                }
            }
        } catch (IOException e1) {
            LOGGER.error("judge package type error ! " + e1.getMessage(), e1);
        }
        if (isXmlCsar) {
            fileType = PACKAGE_XML_FORMAT;
        } else {
            fileType = PACKAGE_YAML_FORMAT;
        }

        return this;
    }

    private void readMarkDown(File file) {
        try {
            InputStream in = FileUtils.openInputStream(file);
            BoundedInputStream boundedInput = new BoundedInputStream(in, 8192);
            InputStreamReader reader = new InputStreamReader(boundedInput, "UTF-8");
            BufferedReader br = new BufferedReader(reader);
            String temp = readLine(br);
            StringBuffer sb = new StringBuffer();
            while (temp != null) {
                sb.append(temp).append("\n");
                temp = readLine(br);
            }
            markDownContent = sb.toString();
        } catch (Exception e) {
            LOGGER.error("readMarkDown exception, {}", e.getMessage());;
        }
    }

    private String readLine(BufferedReader br) throws IOException {
        return read(br, LINE_MAX_LEN);
    }

    private String read(BufferedReader br, int lineMaxLen) throws IOException {
        int intC = br.read();
        if (-1 == intC) {
            return null;
        }
        StringBuilder sb = new StringBuilder(10);
        while (intC != -1) {
            char c = (char) intC;
            if (c == '\n') {
                break;
            }
            if (sb.length() >= lineMaxLen) {
                throw new IOException("line too long");
            }
            sb.append(c);
            intC = br.read();
        }
        String str = sb.toString();
        if (!str.isEmpty() && str.endsWith("\r")) {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    private void readManifest(File file) {
        // Fix the package type to CSAR, temporary
        BufferedReader reader = null;
        InputStreamReader isr = null;
        try {
            BoundedInputStream boundedInput = new BoundedInputStream(FileUtils.openInputStream(file), 8192);
            isr = new InputStreamReader(boundedInput, "UTF-8");
            reader = new BufferedReader(isr, 2048);
            for (String tempString; (tempString = readLine(reader)) != null;) {
                // If line is empty, ignore
                if ("".equals(tempString)) {
                    continue;
                }
                try {
                    int count1 = tempString.indexOf(':');
                    String meta = tempString.substring(0, count1).trim();
                    // Check for the package provider name
                    if (meta.equalsIgnoreCase(MF_PRODUCT_NAME)) {
                        int count = tempString.indexOf(":") + 1;
                        appName = tempString.substring(count).trim();
                    }
                    // Check for the package provider name
                    if (meta.equalsIgnoreCase(MF_PROVIDER_META)) {
                        int count = tempString.indexOf(":") + 1;
                        provider = tempString.substring(count).trim();
                    }
                    // Check for package version
                    if (meta.equalsIgnoreCase(MF_VERSION_META)) {
                        int count = tempString.indexOf(":") + 1;
                        version = tempString.substring(count).trim();
                    }

                    // Check for package version
                    if (meta.equalsIgnoreCase(MF_APP_CONTACT)) {
                        int count = tempString.indexOf(":") + 1;
                        contact = tempString.substring(count).trim();
                    }
                } catch (StringIndexOutOfBoundsException e) {
                    continue;
                }
            }
        } catch (IOException e) {
            LOGGER.error("Exception while parsing manifest file" + e, e);
        } finally {
            try {
                if (isr != null) {
                    isr.close();
                }
            } catch (Exception e) {
                LOGGER.error("Exception while close is" + e, e);
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
                LOGGER.error("Exception while close is" + e, e);
            }
        }
    }

    private boolean isRegularFile(Path filePath) {
        BasicFileAttributes attr = null;
        try {
            attr = Files.readAttributes(filePath, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
            if (!attr.isRegularFile()) {
                LOGGER.error("Not a reqgular file.");
                return false;
            }
        } catch (IOException e) {
            LOGGER.error("Not a reqgular file.");
            return false;
        } catch (Exception e) {
            LOGGER.error("Not a reqgular file.");
            return false;
        }
        return true;
    }

    private static FileRelationResponse buildFileStructure(String root, String base) {
        File rootfile = new File(root);
        if (rootfile != null && !rootfile.isDirectory()) {
            return new FileRelationResponse(rootfile.getName());
        } else {
            FileRelationResponse current = new FileRelationResponse(rootfile.getName());
            File[] list = rootfile.listFiles();
            if (list != null && list.length > 0) {
                for (File subFile : list) {
                    current.addChild(
                            buildFileStructure(subFile.getAbsolutePath(), base + File.separator + rootfile.getName()));
                }
            }
            return current;
        }
    }

    public String getAppName() {
        return appName;
    }

    public String getProvider() {
        return provider;
    }

    public String getVersion() {
        return version;
    }

    public String getFileStructure() {
        return fileStructure;
    }

    public String getContact() {
        return contact;
    }

    public BasicInfo() {}
}