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

package org.edgegallery.atp.model.testcase;

import lombok.Getter;
import lombok.Setter;
import org.edgegallery.atp.constant.Constant;

@Setter
@Getter
public class TestCaseResult {
    /**
     * test case execute result.The value is enum:success,failed or running.
     */
    String result;

    /**
     * test case fail reason,it can be empty when the result is not failed.
     */
    String reason;

    /**
     * verification model.
     */
    String verificationModel;

    /**
     * construct function.
     */
    public TestCaseResult() {
        this.result = Constant.WAITING;
        this.reason = Constant.EMPTY;
        this.verificationModel = Constant.EMPTY;
    }

    /**
     * construct function.
     * 
     * @param result result
     * @param reason reason
     * @param verificationModel verificationModel
     */
    public TestCaseResult(String result, String reason, String verificationModel) {
        this.result = result;
        this.reason = reason;
        this.verificationModel = verificationModel;
    }

    /**
     * construct function.
     * 
     * @param result result
     * @param reason reason
     */
    public TestCaseResult(String result, String reason) {
        this.result = result;
        this.reason = reason;
    }
}
