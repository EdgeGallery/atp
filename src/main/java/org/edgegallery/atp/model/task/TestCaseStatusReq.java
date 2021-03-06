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

package org.edgegallery.atp.model.task;

import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.edgegallery.atp.constant.Constant;

@Getter
@Setter
public class TestCaseStatusReq {
    private String testScenarioId;
    private String testSuiteId;
    private String testCaseId;
    private String result;

    @Size(max = Constant.LENGTH_255)
    private String reason;
}
