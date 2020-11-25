package org.edgegallery.atp.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.commons.lang3.StringUtils;
import org.edgegallery.atp.constant.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test Case Util class
 */
public class TestCaseUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestCaseUtil.class);

    /**
     * validate fileName is .pattern
     * 
     * @param pattern filePattern
     * @param fileName fileName
     * @return
     */
    public static boolean fileSuffixValidate(String pattern, String fileName) {
        String suffix = fileName.substring(fileName.indexOf(Constant.DOT) + 1);
        if (StringUtils.isNotBlank(suffix)) {
            if (suffix.equals(pattern)) {
                return true;
            }
        }
        return false;
    }

    /**
     * get file path which key is included in prefixSet
     * 
     * @param zipFile zipFile
     * @param entry entry
     * @param prefixSet prefixSet
     * @return
     */
    public static Set<String> getPathSet(ZipFile zipFile, ZipEntry entry, Set<String> prefixSet) {
        Set<String> pathSet = new HashSet<String>();
        try (BufferedReader br =
                new BufferedReader(new InputStreamReader(zipFile.getInputStream(entry), StandardCharsets.UTF_8))) {
            String line = "";
            while ((line = br.readLine()) != null) {
                String[] splitByColon = line.split(Constant.COLON);
                // prefix: path
                if (splitByColon.length > 1 && prefixSet.contains(splitByColon[0])) {
                    pathSet.add(splitByColon[1].trim());
                }
            }
        } catch (IOException e) {
            LOGGER.error("getPathSet io exception. {}", e.getMessage());
        }

        return pathSet;
    }

    /**
     * validate the fields in file conclude the fields in prefixSet all
     * 
     * @param zipFile zipFile
     * @param entry entry
     * @param prefixSet entry
     * @return
     */
    public static boolean isExistAll(ZipFile zipFile, ZipEntry entry, Set<String> prefixSet) {
        Set<String> sourcePathSet = new HashSet<String>();
        try (BufferedReader br =
                new BufferedReader(new InputStreamReader(zipFile.getInputStream(entry), StandardCharsets.UTF_8))) {
            String line = "";
            while ((line = br.readLine()) != null) {
                // prefix: path
                String[] splitByColon = line.split(Constant.COLON);
                if (splitByColon.length > 1 && prefixSet.contains(splitByColon[0].trim())) {
                    sourcePathSet.add(splitByColon[0].trim());
                }
            }
        } catch (IOException e) {
            LOGGER.error("getPathSet io exception. {}", e.getMessage());
        }

        return sourcePathSet.containsAll(prefixSet);
    }

    /**
     * remove last slash in path.Suit for pattern of Artifacts/test,not Artifacts/test/
     * 
     * @param path path
     * @return
     */
    public static String removeLastSlash(String path) {
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    /**
     * get app_name and app_archive_version from .mf file.
     * 
     * @param filePath
     * @return
     */
    public static Map<String, String> getAppNameAndVersion(String filePath) {
        Map<String, String> resultMap = new HashMap<String, String>();
        try (ZipFile zipFile = new ZipFile(filePath)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.getName().split("/").length == 2 && TestCaseUtil.fileSuffixValidate("mf", entry.getName())) {
                    try (BufferedReader br = new BufferedReader(
                            new InputStreamReader(zipFile.getInputStream(entry), StandardCharsets.UTF_8))) {
                        String line = "";
                        while ((line = br.readLine()) != null) {
                            // prefix: path
                            String[] splitByColon = line.split(Constant.COLON);
                            if (splitByColon.length > 1 && Constant.APP_NAME.equals(splitByColon[0].trim())) {
                                resultMap.put(Constant.APP_NAME, splitByColon[1].trim());
                            }

                            if (splitByColon.length > 1 && Constant.APP_VERSION.equals(splitByColon[0].trim())) {
                                resultMap.put(Constant.APP_VERSION, splitByColon[1].trim());
                            }
                        }
                    } catch (IOException e) {
                        LOGGER.error("getAppNameAndVersion io exception. {}", e.getMessage());
                    }

                }
            }
        } catch (IOException e) {
            LOGGER.error("getAppNameAndVersion execute failed. {}", e.getMessage());
        }
        return resultMap;
    }

}
