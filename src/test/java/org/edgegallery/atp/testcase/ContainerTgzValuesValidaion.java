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

package org.edgegallery.atp.testcase;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

/**
 * validate existence of values.yaml in tgz fille.
 */
public class ContainerTgzValuesValidaion {
    private static final String INNER_EXCEPTION = "inner exception, please check the log.";

    private static final String TGZ_NOT_EXISTS = "there is no .tgz file in Artifacts/Deployment/Charts dir";

    private static final String VALUES_NOT_EXISTS_IN_TGZ
        = "there is no values.yaml file in .tgz file of Artifacts/Deployment/Charts dir";

    private static final String VM = "vm";

    private static final String SUCCESS = "success";

    /**
     * execute test case.
     *
     * @param filePath csar file path
     * @param context context info
     * @return execute result
     */
    public String execute(String filePath, Map<String, String> context) {
        delay();
        //vm app does not has helm chart file.
        if (VM.equalsIgnoreCase(getAppType(filePath))) {
            return SUCCESS;
        }
        try (ZipFile zipFile = new ZipFile(filePath)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.getName().startsWith("Artifacts/Deployment/Charts") && entry.getName().endsWith(".tgz")) {
                    return null == analysizeTgz(zipFile, entry) ? VALUES_NOT_EXISTS_IN_TGZ : SUCCESS;
                }
            }
        } catch (IOException e) {
            return INNER_EXCEPTION;
        }
        return TGZ_NOT_EXISTS;
    }

    /**
     * analysize tgz file content.
     *
     * @param zipFile zipFile
     * @param entry entry
     * @return if existing templates dir
     */
    private String analysizeTgz(ZipFile zipFile, ZipEntry entry) {
        try (TarArchiveInputStream tarIn = new TarArchiveInputStream(
            new GzipCompressorInputStream(new BufferedInputStream(zipFile.getInputStream(entry))))) {
            TarArchiveEntry entryTar = null;
            while ((entryTar = tarIn.getNextTarEntry()) != null) {
                String[] nameArray = entryTar.getName().split("/");
                if ("values.yaml".equalsIgnoreCase(nameArray[nameArray.length - 1])) {
                    return SUCCESS;
                }
            }
        } catch (IOException e) {
        }

        return null;
    }

    /**
     * get app_type.
     *
     * @param filePath filePath
     * @return appType appType
     */
    private String getAppType(String filePath) {
        try (ZipFile zipFile = new ZipFile(filePath)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.getName().split("/").length == 1 && entry.getName().endsWith(".mf")) {
                    return analysizeMfAndGetAppClass(zipFile, entry);
                }
            }
        } catch (IOException e) {
        }
        return null;
    }

    /**
     * analysize mf file and get app class value.
     *
     * @param zipFile zipFile
     * @param entry entry
     * @return file type
     */
    private String analysizeMfAndGetAppClass(ZipFile zipFile, ZipEntry entry) {
        try (BufferedReader br = new BufferedReader(
            new InputStreamReader(zipFile.getInputStream(entry), StandardCharsets.UTF_8))) {
            String line = "";
            while ((line = br.readLine()) != null) {
                // prefix: path
                if (line.trim().startsWith("app_class")) {
                    return line.split(":")[1].trim();
                }
            }
        } catch (IOException e) {
        }
        return null;
    }

    /**
     * add delay.
     */
    private void delay() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }
    }
}
