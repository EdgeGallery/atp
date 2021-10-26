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

import java.util.Map;
import org.edgegallery.atp.model.task.testscenarios.TaskTestCase;
import org.edgegallery.atp.model.testcase.TestCase;

/**
 * test case executor interface.
 */
public interface TestCaseExecutor {
    String EXECUTE = "execute";

    String TEST_CASE_CLASS = "TestCase.class";

    /**
     * execute test case scripts.
     *
     * @param testCase testCase
     * @param csarFilePath csarFilePath
     * @param taskTestCase taskTestCase
     * @param context context
     */
    public void executeTestCase(TestCase testCase, String csarFilePath, TaskTestCase taskTestCase,
        Map<String, String> context);
}
