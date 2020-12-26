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

package org.edgegallery.atp.schedule.testcase;

import java.util.Map;
import org.edgegallery.atp.model.testcase.TestCaseResult;
import org.springframework.stereotype.Component;

/**
 * test case module class
 *
 */
@Component
public abstract class TestCaseAbs {

    /**
     * execute test case method.
     * 
     * @param filePath filePath
     * @param context context info
     * @return
     */
    public abstract TestCaseResult execute(String filePath, Map<String, String> context);

    /**
     * set return result.
     * 
     * @param result task result
     * @param reason failed reason
     * @param testCaseresult return result model
     * @return return result model
     */
    public TestCaseResult setTestCaseResult(String result, String reason, TestCaseResult testCaseresult) {
        testCaseresult.setReason(reason);
        testCaseresult.setResult(result);
        return testCaseresult;
    }

}
