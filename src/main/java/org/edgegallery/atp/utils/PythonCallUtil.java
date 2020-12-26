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
            else if (Constant.SUCCESS.equalsIgnoreCase(pyobj.toString())) {
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
