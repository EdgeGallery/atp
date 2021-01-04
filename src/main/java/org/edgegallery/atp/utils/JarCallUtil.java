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
package org.edgegallery.atp.utils;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.apache.commons.lang3.StringUtils;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.constant.ExceptionConstant;
import org.edgegallery.atp.model.testcase.TestCase;
import org.edgegallery.atp.model.testcase.TestCaseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JarCallUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(JarCallUtil.class);

    private static final String TEST_CASE_CLASS = "TestCase.class";

    public static void executeJar(TestCase testCase, String csarFilePath, TestCaseResult result,
            Map<String, String> context) {
        try (JarFile jarFile = new JarFile(new File(testCase.getFilePath()));
                URLClassLoader classLoader = new URLClassLoader(new URL[] {new URL("file:" + testCase.getFilePath())},
                        Thread.currentThread().getContextClassLoader());) {
            Enumeration<JarEntry> entries = jarFile.entries();

            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                String name = jarEntry.getName();
                if (StringUtils.isNotEmpty(name) && name.endsWith(TEST_CASE_CLASS)) {
                    Class<?> clazz = classLoader
                            .loadClass(name.replace(Constant.SLASH, Constant.DOT).substring(0, name.length() - 6));
                    Object response = clazz.getMethod("execute", String.class, Map.class).invoke(clazz.newInstance(),
                            csarFilePath, context);
                    if (null == response) {
                        LOGGER.error(ExceptionConstant.METHOD_RETURN_IS_NULL);
                        result.setResult(Constant.FAILED);
                        result.setReason(ExceptionConstant.METHOD_RETURN_IS_NULL);
                    } else if (Constant.SUCCESS.equalsIgnoreCase(response.toString())) {
                        result.setResult(Constant.SUCCESS);
                    } else {
                        result.setResult(Constant.FAILED);
                        result.setReason(response.toString());
                    }
                }
            }

        } catch (Exception e) {
            LOGGER.error("call jar failed.", e);
            result.setResult(Constant.FAILED);
            result.setReason("execute jar failed.");
        }
    }
}
