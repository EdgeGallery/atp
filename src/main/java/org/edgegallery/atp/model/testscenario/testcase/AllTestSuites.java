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

package org.edgegallery.atp.model.testscenario.testcase;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.edgegallery.atp.model.testcase.TestCase;
import org.edgegallery.atp.model.testsuite.TestSuite;

@Getter
@Setter
public class AllTestSuites extends TestSuite {
    List<TestCase> testCases;

    /**
     * construct function.
     * 
     * @param testSuite testSuite
     */
    public AllTestSuites(TestSuite testSuite) {
        this.setDescriptionCh(testSuite.getDescriptionCh());
        this.setDescriptionEn(testSuite.getDescriptionEn());
        this.setId(testSuite.getId());
        this.setNameCh(testSuite.getNameCh());
        this.setNameEn(testSuite.getNameEn());
        this.setScenarioIdList(testSuite.getScenarioIdList());
    }
}
