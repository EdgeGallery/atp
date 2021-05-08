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

package org.edgegallery.atp.model.testcase;

import java.util.Arrays;
import lombok.Getter;
import lombok.Setter;
import org.edgegallery.atp.constant.Constant;

/**
 * Test Case DB Model.
 */
@Getter
@Setter
public class TestCasePo extends TestCaseBase {
    private String testSuiteIdList;

    /**
     * model transfer.
     * 
     * @return test case
     */
    public TestCase toDomain() {
        TestCase testCase = TestCase.builder().setClassName(this.getClassName()).setCodeLanguage(this.getCodeLanguage())
                .setDescriptionCh(this.getDescriptionCh()).setDescriptionEn(this.getDescriptionEn())
                .setExpectResultCh(this.getExpectResultCh()).setExpectResultEn(this.getExpectResultEn())
                .setFilePath(this.getFilePath()).setHashCode(this.getHashCode()).setId(this.getId())
                .setNameCh(this.getNameCh()).setNameEn(this.getNameEn()).setTestStepCh(this.getTestStepCh())
                .setTestStepEn(this.getTestStepEn()).setType(this.getType()).build().toTestCase();

        testCase.setTestSuiteIdList(Arrays.asList(this.getTestSuiteIdList().split(Constant.COMMA)));
        return testCase;
    }
}
