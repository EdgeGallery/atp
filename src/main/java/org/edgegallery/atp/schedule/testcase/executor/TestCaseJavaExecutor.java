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

package org.edgegallery.atp.schedule.testcase.executor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import org.apache.commons.io.FileUtils;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.model.task.testscenarios.TaskTestCase;
import org.edgegallery.atp.model.testcase.TestCase;
import org.edgegallery.atp.utils.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * dynamic compile java file.
 */
public class TestCaseJavaExecutor implements TestCaseExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestCaseJavaExecutor.class);

    @Override
    public void executeTestCase(TestCase testCase, String csarFilePath, TaskTestCase taskTestCase,
        Map<String, String> context) {
        try {
            String className = testCase.getClassName();
            String javaSrcFile = FileUtils.readFileToString(new File(testCase.getFilePath()), StandardCharsets.UTF_8);
            Map<String, byte[]> bytes = compile(className.concat(Constant.DOT).concat(Constant.JAVA), javaSrcFile);

            try (TestCaseJavaExecutor.AtpClassLoader clsLoader = new TestCaseJavaExecutor.AtpClassLoader(bytes);) {
                Class<?> clazz = clsLoader.loadClass(className);
                Object response = clazz.getMethod(EXECUTE, String.class, Map.class)
                    .invoke(clazz.newInstance(), csarFilePath, context);
                CommonUtil.setResult(response, taskTestCase);
            }
        } catch (Exception e) {
            LOGGER.error("dynamic compile failed. {}", e);
            taskTestCase.setResult(Constant.FAILED);
            taskTestCase.setReason("dynamic compile failed.");
        }
    }

    /**
     * compile code.
     *
     * @param className className
     * @param javaSrc javaSrc
     * @return map
     */
    public Map<String, byte[]> compile(String className, String javaSrc) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager javaFileMgr = compiler.getStandardFileManager(null, null, null);

        try (AtpFileManager memoryMgr = new AtpFileManager(javaFileMgr)) {
            JavaFileObject javaFileObject = memoryMgr.getSourceFromStr(className, javaSrc);
            JavaCompiler.CompilationTask compileTask = compiler
                .getTask(null, memoryMgr, null, null, null, Arrays.asList(javaFileObject));
            if (compileTask.call()) {
                return memoryMgr.getClassBytes();
            }
        } catch (IOException e) {
            LOGGER.error("dynamic compile failed.{}", e);
        }
        return null;
    }

    /**
     * defineClass method to load class.
     */
    private class AtpClassLoader extends URLClassLoader {
        Map<String, byte[]> sourceCode = new HashMap<String, byte[]>();

        public AtpClassLoader(Map<String, byte[]> classBytes) {
            super(new URL[0], TestCaseJavaExecutor.AtpClassLoader.class.getClassLoader());
            this.sourceCode.putAll(classBytes);
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            byte[] bytes = sourceCode.get(name);
            if (bytes == null) {
                return super.findClass(name);
            }
            sourceCode.remove(name);
            return defineClass(name, bytes, 0, bytes.length);
        }
    }

    /**
     * construction of file manager.
     */
    private class AtpFileManager extends ForwardingJavaFileManager {
        private static final String MFM = "atp:///";

        private Map<String, byte[]> classMap;

        public AtpFileManager(JavaFileManager fileManager) {
            super(fileManager);
            classMap = new HashMap<String, byte[]>();
        }

        @Override
        public JavaFileObject getJavaFileForOutput(JavaFileManager.Location location, String className,
            JavaFileObject.Kind kind, FileObject sibling) throws IOException {
            if (kind == JavaFileObject.Kind.CLASS) {
                return new atpJavaFileObject(className, null);
            } else {
                return super.getJavaFileForOutput(location, className, kind, sibling);
            }
        }

        @Override
        public void close() throws IOException {
            classMap = new HashMap<String, byte[]>();
        }

        /**
         * return class bytecode.
         *
         * @return classMap
         */
        public Map<String, byte[]> getClassBytes() {
            return classMap;
        }

        /**
         * get javaFileObject of source code.
         *
         * @param className className
         * @param srcCode srcCode
         * @return JavaFileObject
         */
        public JavaFileObject getSourceFromStr(String className, String srcCode) {
            return new atpJavaFileObject(className, srcCode);
        }

        /**
         * construction of source code file object.
         */
        private class atpJavaFileObject extends SimpleJavaFileObject {
            private String sourceCode;

            private String className;

            atpJavaFileObject(String className, String sourceCode) {
                super(convertUri(className), Kind.SOURCE);
                this.sourceCode = null == sourceCode ? this.sourceCode : sourceCode;
                this.className = className;
            }

            @Override
            public CharBuffer getCharContent(boolean ignoreEncodingErrors) {
                return CharBuffer.wrap(sourceCode);
            }

            @Override
            public OutputStream openOutputStream() {
                return new FilterOutputStream(new ByteArrayOutputStream()) {
                    @Override
                    public void close() throws IOException {
                        ByteArrayOutputStream bos = (ByteArrayOutputStream) out;
                        classMap.put(className, bos.toByteArray());
                        out.close();
                    }
                };
            }
        }

        /**
         * convert URI.
         *
         * @param className className
         * @return URI
         */
        private URI convertUri(String className) {
            File file = new File(className);
            return file.exists() ? file.toURI() : URI.create(MFM.concat(className));
        }
    }
}



