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

package org.edgegallery.atp.repository.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.edgegallery.atp.model.testscenario.TestScenario;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface TestScenarioMapper {
    /**
     * create test scenario.
     * 
     * @param testScenario testScenario
     */
    void createTestScenario(TestScenario testScenario);

    /**
     * get test scenario by name.
     * 
     * @param nameCh nameCh
     * @param nameEn nameEn
     * @return TestScenario
     */
    TestScenario getTestScenarioByName(@Param("nameCh") String nameCh, @Param("nameEn") String nameEn);

    /**
     * get all test scenarios, name is fuzzy query.
     * 
     * @param nameCh nameCh
     * @param nameEn nameEn
     * @return TestScenario list
     */
    List<TestScenario> getAllTestScenario(@Param("nameCh") String nameCh, @Param("nameEn") String nameEn);

    /**
     * update test scenario.
     * 
     * @param testScenario testScenario
     */
    void updateTestScenario(TestScenario testScenario);
    
    /**
     * get test scenario by id.
     * 
     * @param id id
     * @return test scenario info
     */
    TestScenario getTestScenarioById(String id);

    /**
     * delete test scenario by id.
     * 
     * @param id test scenario id
     */
    void deleteTestScenario(String id);

    /**
     * batch query test scenario by ids.
     * 
     * @param ids test scenario ids
     * @return test scenario list
     */
    List<TestScenario> batchQueryTestScenario(@Param("ids") List<String> ids);
}
