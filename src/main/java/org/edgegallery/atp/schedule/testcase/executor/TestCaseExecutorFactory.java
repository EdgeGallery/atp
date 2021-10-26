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

import org.edgegallery.atp.constant.Constant;

/**
 * test case executor factory.
 */
public class TestCaseExecutorFactory {
    private static TestCaseExecutorFactory instance = new TestCaseExecutorFactory();

    private TestCaseExecutorFactory() {
    }

    /**
     * get TestCaseExecutorFactory instance.
     *
     * @return TestCaseExecutorFactory instance
     */
    public static synchronized TestCaseExecutorFactory getInstance() {
        return instance;
    }

    /**
     * generate specific test case executor according to code language.
     *
     * @param codeLanguage codeLanguage
     * @return specific test case executor
     */
    public TestCaseExecutor generateExecutor(String codeLanguage) {
        //codeLanguage has validated before, the value must be java,jar or python
        switch (codeLanguage) {
            case Constant.JAVA:
                return new TestCaseJavaExecutor();
            case Constant.PYTHON:
                return new TestCasePyExecutor();
            case Constant.JAR:
                return new TestCaseJarExecutor();
        }
        return new TestCaseJavaExecutor();
    }
}
