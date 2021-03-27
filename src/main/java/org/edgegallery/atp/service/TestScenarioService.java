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

package org.edgegallery.atp.service;

import java.io.FileNotFoundException;
import java.util.List;
import org.edgegallery.atp.model.testscenario.TestScenario;
import org.edgegallery.atp.model.testscenario.testcase.AllTestScenarios;
import org.springframework.web.multipart.MultipartFile;

public interface TestScenarioService {
    /**
     * create test scenario.
     * 
     * @param testScenario test scenario info
     * @return test scenario info
     */
    TestScenario createTestScenario(TestScenario testScenario, MultipartFile icon);

    /**
     * update test scenario.
     * 
     * @param testScenario test scenario info
     * @return test scenario
     */
    TestScenario updateTestScenario(TestScenario testScenario, MultipartFile icon);

    /**
     * delete test scenario by id.
     * 
     * @param id test scenario id
     * @return true
     */
    Boolean deleteTestScenario(String id);

    /**
     * get test scenario by id.
     * 
     * @param id test scenario id
     * @return test scenario info
     */
    TestScenario getTestScenario(String id) throws FileNotFoundException;

    /**
     * get all test scenarios.
     * 
     * @param locale locale language
     * @param name test case name
     * @return test scenario list
     */
    List<TestScenario> queryAllTestScenario(String locale, String name);

    /**
     * get all test cases according to scenario ids.
     * 
     * @param ids test scenario ids
     * @return test scenario info
     */
    List<AllTestScenarios> getTestCasesByScenarioIds(List<String> ids);
}
