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

package org.edgegallery.atp.model.testcase;

import java.util.List;
import java.util.stream.Collectors;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * Test Case Request Model
 */
@Getter
@Setter
@JsonIgnoreProperties(value = {"filePath", "className"})
public class TestCase extends TestCaseBase {
    /**
     * test suite id list the test case belong to.
     */
    private List<String> testSuiteIdList;

    public TestCasePo of() {
        TestCasePo testCasePo =
                TestCasePo.builder().setClassName(this.getClassName()).setCodeLanguage(this.getCodeLanguage())
                        .setdescriptionCh(this.getDescriptionCh()).setDescriptionEn(this.getDescriptionEn())
                        .setExpectResultCh(this.getExpectResultCh()).setExpectResultEn(this.getExpectResultEn())
                        .setFilePath(this.getFilePath()).setHashCode(this.getHashCode()).setId(this.getId())
                        .setNameCh(this.getNameCh()).setNameEn(this.getNameEn()).setTestStepCh(this.getTestStepCh())
                        .setTestStepEn(this.getTestStepEn()).setType(this.getType()).build().toTestCasePo();
        testCasePo.setTestSuiteIdList(this.getTestSuiteIdList().stream().collect(Collectors.joining(",")));
        return testCasePo;
    }
}
