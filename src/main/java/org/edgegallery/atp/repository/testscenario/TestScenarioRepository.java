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

package org.edgegallery.atp.repository.testscenario;

import java.util.List;
import org.edgegallery.atp.model.testscenario.TestScenario;

public interface TestScenarioRepository {
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
    TestScenario getTestScenarioByName(String nameCh, String nameEn);

    /**
     * get test scenario by id.
     * 
     * @param id id
     * @return TestScenario
     */
    TestScenario getTestScenarioById(String id);

    /**
     * update test scenario.
     * 
     * @param testScenario testScenario
     */
    void updateTestScenario(TestScenario testScenario);

    /**
     * delete test scenario by id.
     * 
     * @param id test scenario id
     */
    void deleteTestScenario(String id);
    
    /**
     * get all test scenarios.
     * 
     * @param locale locale
     * @param name test scenario name
     * @return test scenario list
     */
    List<TestScenario> getAllTestScenarios(String locale, String name);

    /**
     * batch query test scenario by ids.
     * 
     * @param ids test scenario ids
     * @return test scenario list
     */
    List<TestScenario> batchQueryTestScenario(List<String> ids);
}
