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

package org.edgegallery.atp.model.testsuite;

import java.util.Arrays;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import org.edgegallery.atp.constant.Constant;

@Getter
@Setter
public class TestSuitePo {
    private String id;

    private String nameCh;

    private String nameEn;

    private String descriptionCh;

    private String descriptionEn;

    private String scenarioIdList;

    public TestSuitePo() {

    }

    /**
     * construct function.
     * 
     * @param testSuite test suite
     */
    public TestSuitePo(TestSuite testSuite) {
        this.id = testSuite.getId();
        this.nameCh = testSuite.getNameCh();
        this.nameEn = testSuite.getNameEn();
        this.descriptionCh = testSuite.getDescriptionCh();
        this.descriptionEn = testSuite.getDescriptionEn();
        scenarioIdList = testSuite.getScenarioIdList().stream().collect(Collectors.joining(Constant.COMMA));
    }

    /**
     * model transfer.
     * 
     * @return test suite
     */
    public TestSuite toDomain() {
        TestSuite testSuite =
                TestSuite.builder().setdescriptionCh(this.descriptionCh).setDescriptionEn(this.descriptionEn)
                        .setId(this.id).setnameCh(this.nameCh).setNameEn(this.nameEn).build();
        testSuite.setScenarioIdList(Arrays.asList(scenarioIdList.split(Constant.COMMA)));
        return testSuite;
    }
}
