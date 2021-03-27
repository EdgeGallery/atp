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

package org.edgegallery.atp.model.testscenario.testcase;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.edgegallery.atp.model.testscenario.TestScenario;

@Getter
@Setter
public class AllTestScenarios extends TestScenario {
    List<AllTestSuites> testSuites;

    /**
     * construct function.
     * 
     * @param testScenario testScenario
     */
    public AllTestScenarios(TestScenario testScenario) {
        this.setDescriptionCh(testScenario.getDescriptionCh());
        this.setDescriptionEn(testScenario.getDescriptionEn());
        this.setId(testScenario.getId());
        this.setNameCh(testScenario.getNameCh());
        this.setNameEn(testScenario.getNameEn());
        this.setLabel(testScenario.getLabel());
    }
}
