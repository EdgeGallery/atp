package org.edgegallery.atp.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.constant.ExceptionConstant;
import org.edgegallery.atp.model.testcase.TestCase;
import org.edgegallery.atp.model.testcase.TestCaseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * java dynamic compile util
 *
 */
public class JavaCompileUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(JavaCompileUtil.class);

    public static void executeJava(TestCase testCase, String csarFilePath, TestCaseResult result,
            Map<String, String> context) {
        try {
            String className = testCase.getClassName();
            Map<String, byte[]> bytes =
                    compile(className.concat(Constant.DOT).concat(Constant.JAVA),
                            getFileContent(testCase.getFilePath()));
            // put class into storage
            try (JavaCompileUtil.MemoryLoader clsLoader = new JavaCompileUtil.MemoryLoader(bytes);) {
                Class<?> clazz = clsLoader.loadClass(className);
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

        } catch (Exception e) {
            LOGGER.error("dynamic compile failed. {}", e.getMessage());
        }

    }

    private static String getFileContent(String path) throws IOException {
        StringBuffer result = new StringBuffer();
        try (BufferedReader buffer = new BufferedReader(new FileReader(path))) {
            String line = null;
            while ((line = buffer.readLine()) != null) {
                result.append(line.trim()).append("\r");
            }
        }
        return result.toString();
    }

    public static Map<String, byte[]> compile(String javaName, String javaSrc) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager javaFileMgr = compiler
                .getStandardFileManager(null, null, null);

        try (JavaFileMemoryMgr memoryMgr = new JavaFileMemoryMgr(
                javaFileMgr)) {
            JavaFileObject javaFileObject = memoryMgr.getSourceFromStr(javaName,
                    javaSrc);
            JavaCompiler.CompilationTask compileTask = compiler.getTask(null, memoryMgr,
                    null, null, null, Arrays.asList(javaFileObject));
            if (compileTask.call()) {
                return memoryMgr.getClassBytes();
            }

        } catch (IOException e) {
            LOGGER.error("dynamic compile failed.{}", e);
        }
        return null;
    }

    /**
     * defineClass method to load class
     */
    private static class MemoryLoader extends URLClassLoader {
        Map<String, byte[]> sourceCode = new HashMap<String, byte[]>();

        public MemoryLoader(Map<String, byte[]> classBytes) {
            super(new URL[0], MemoryLoader.class.getClassLoader());
            this.sourceCode.putAll(classBytes);
        }

        @Override
        protected Class<?> findClass(String name)
                throws ClassNotFoundException {
            byte[] bytes = sourceCode.get(name);
            if (bytes == null) {
                return super.findClass(name);
            }
            sourceCode.remove(name);
            return defineClass(name, bytes, 0, bytes.length);
        }
    }

}
