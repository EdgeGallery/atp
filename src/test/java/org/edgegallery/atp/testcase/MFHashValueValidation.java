package org.edgegallery.atp.testcase;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * mf file hash value validation.
 *
 */
public class MFHashValueValidation {
    private static final Logger LOGGER = LoggerFactory.getLogger(MFHashValueValidation.class);
    private static final String HASH_VALUE_NOT_RIGHT = "hash value in mf file not right.";
    private static final String INNER_EXCEPTION = "inner exception, please check the log.";
    private static final String SOURCE = "Source";

    public String execute(String filePath, Map<String, String> context) {
        Map<String,String> file2Hash = new HashMap<String,String>();
        try (ZipFile zipFile = new ZipFile(filePath)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.getName().split("/").length == 1 &&  entry.getName().endsWith(".mf")) {
                    getFileHash(zipFile, entry, file2Hash);
                }
            }
        } catch (IOException e) {
            return INNER_EXCEPTION;
        }
        
        ZipEntry entry;
        try (FileInputStream fis = new FileInputStream(filePath);
                ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis))) {
            while ((entry = zis.getNextEntry()) != null) {
                if (null != file2Hash.get(entry.getName())) {
                    byte[] data = getByte(zis);
                    InputStream inputStream = new ByteArrayInputStream(data);
                    String hashValue = DigestUtils.sha256Hex(inputStream);
                    if (!file2Hash.get(entry.getName()).equals(hashValue)) {
                        return HASH_VALUE_NOT_RIGHT;
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("get file hash failed {}", e);
        }

        return "success";
    }

    /**
     * get bytes from inputStream.
     * 
     * @param zis inputStream
     * @return file bytes
     */
    public byte[] getByte(InflaterInputStream zis) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int count = 0;
            while ((count = zis.read(buffer, 0, 1024)) != -1) {
                outputStream.write(buffer, 0, count);
            }
            return outputStream.toByteArray();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * get source file path and its hash value.
     * 
     * @param zipFile zipFile
     * @param entry entry
     * @param file2Hash key:filePath value:hashValue
     */
    private void getFileHash(ZipFile zipFile, ZipEntry entry, Map<String, String> file2Hash) {
        try (BufferedReader br =
                new BufferedReader(new InputStreamReader(zipFile.getInputStream(entry), StandardCharsets.UTF_8))) {
            String line = "";
            while ((line = br.readLine()) != null) {
                String[] splitByColon = line.split(":");
                // Source: path
                if (splitByColon.length > 1 && SOURCE.equals((splitByColon[0]))) {
                    boolean flag = true;
                    String filePath = splitByColon[1].trim();
                    while ((line = br.readLine()) != null) {
                        String[] array = line.split(":");
                        if (SOURCE.equals((array[0].trim()))) {
                            if (flag) {
                                file2Hash.put(filePath.trim(), "");
                            } else {
                                flag = true;
                            }
                            filePath = array[0].trim();
                        }
                        if ("Hash".equals((array[0]))) {
                            file2Hash.put(filePath, array[1].trim());
                            flag = false;
                        }
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("get file hash failed.{}", e);
        }
    }
}
