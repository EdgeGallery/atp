package org.edgegallery.atp.utils;

import java.util.Map;
import java.util.Properties;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.constant.ExceptionConstant;
import org.edgegallery.atp.model.testcase.TestCaseResult;
import org.python.core.PyFunction;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PythonCallUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(PythonCallUtil.class);

    public static void callPython(String testCasePath, String csarFilePath, TestCaseResult result,
            Map<String, String> context) {
        LOGGER.info("start call Python");
        try {
            Properties props = new Properties();
            props.put("python.import.site", "false");
            Properties preprops = System.getProperties();
            PythonInterpreter.initialize(preprops, props, new String[0]);

            PythonInterpreter interpreter = new PythonInterpreter();
            interpreter.execfile(testCasePath);

            PyFunction pyFunction = interpreter.get("execute", PyFunction.class);
            PyObject pyobj = pyFunction.__call__(new PyString(csarFilePath), new PyString(context.toString()));
            if (null == pyobj) {
                LOGGER.error(ExceptionConstant.METHOD_RETURN_IS_NULL);
                result.setResult(Constant.FAILED);
                result.setReason(ExceptionConstant.METHOD_RETURN_IS_NULL);
            }
            if (Constant.SUCCESS.equalsIgnoreCase(pyobj.toString())) {
                result.setResult(Constant.SUCCESS);
            } else {
                result.setResult(Constant.FAILED);
                result.setReason(pyobj.toString());
            }
        }
        catch (Exception e) {
            LOGGER.error("python error. {}", e);
        }

    }

}
