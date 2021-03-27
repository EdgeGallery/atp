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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.Map;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import org.edgegallery.atp.constant.Constant;

/**
 * put class into map.
 */
public final class JavaFileMemoryMgr extends ForwardingJavaFileManager {

    private static final String MFM = "mfm:///";

    private static final String MFM_JAVA_SOURCE = "mfm:///com/sun/script/java/java_source";

    private Map<String, byte[]> classMap;

    public JavaFileMemoryMgr(JavaFileManager fileManager) {
        super(fileManager);
        classMap = new HashMap<String, byte[]>();
    }

    @Override
    public void flush() throws IOException {
    }

    @Override
    public JavaFileObject getJavaFileForOutput(JavaFileManager.Location location, String className,
            JavaFileObject.Kind kind, FileObject sibling) throws IOException {
        if (kind == JavaFileObject.Kind.CLASS) {
            return new ClassCodeBuffer(className);
        } else {
            return super.getJavaFileForOutput(location, className, kind, sibling);
        }
    }

    @Override
    public void close() throws IOException {
        classMap = new HashMap<String, byte[]>();
    }

    public Map<String, byte[]> getClassBytes() {
        return classMap;
    }

    /**
     * convert URI.
     * 
     * @param className className
     * @return URI
     */
    public static URI convertUri(String className) {
        File file = new File(className);
        if (file.exists()) {
            return file.toURI();
        } else {
            try {
                final StringBuffer result = new StringBuffer();
                result.append(MFM).append(className.replace(Constant.DOT, Constant.SLASH));
                if (className.endsWith(Constant.JAVA_FILE)) {
                    result.replace(result.length() - Constant.JAVA_FILE.length(), result.length(), Constant.JAVA_FILE);
                }
                return URI.create(result.toString());
            } catch (Exception e) {
                return URI.create(MFM_JAVA_SOURCE);
            }
        }
    }

    /**
     * get Source From String.
     * 
     * @param className className
     * @param srcCode srcCode
     * @return JavaFileObject
     */
    public static JavaFileObject getSourceFromStr(String className, String srcCode) {
        return new SourceCodeBuffer(className, srcCode);
    }

    private static class SourceCodeBuffer extends SimpleJavaFileObject {
        final String sourceCode;

        SourceCodeBuffer(String name, String sourceCode) {
            super(convertUri(name), Kind.SOURCE);
            this.sourceCode = sourceCode;
        }

        @Override
        public CharBuffer getCharContent(boolean ignoreEncodingErrors) {
            return CharBuffer.wrap(sourceCode);
        }
    }

    private class ClassCodeBuffer extends SimpleJavaFileObject {
        private String className;

        ClassCodeBuffer(String className) {
            super(convertUri(className), Kind.CLASS);
            this.className = className;
        }

        @Override
        public OutputStream openOutputStream() {
            return new FilterOutputStream(new ByteArrayOutputStream()) {
                @Override
                public void close() throws IOException {
                    out.close();
                    ByteArrayOutputStream bos = (ByteArrayOutputStream) out;
                    classMap.put(className, bos.toByteArray());
                }
            };
        }
    }
}
