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
package org.edgegallery.atp.repository.testsuite;

import java.util.List;
import org.edgegallery.atp.model.testsuite.TestSuite;

public interface TestSuiteRepository {
    /**
     * create test suite
     * 
     * @param testSuite testSuite
     */
    void createTestSuite(TestSuite testSuite);

    /**
     * get test suite by name
     * 
     * @param name
     * @return
     */
    TestSuite getTestSuiteByName(String nameCh, String nameEn);

    /**
     * get test suite by id
     * 
     * @param id
     * @return
     */
    TestSuite getTestSuiteById(String id);

    /**
     * update test suite
     * 
     * @param testSuite testSuite
     * @return testSuite info
     */
    void updateTestSuite(TestSuite testSuite);

    /**
     * delete test suite by id
     * 
     * @param id test suite id
     */
    void deleteTestSuite(String id);
    
    /**
     * get all test suites
     * 
     * @param locale locale
     * @param name test suite name
     * @param scenarioId scenario id test suite belongs to
     * @return test suite list
     */
    List<TestSuite> getAllTestSuites(String locale, String name, String scenarioId);

    /**
     * batch query test suites
     * 
     * @param ids test suite ids
     * @return test suite list
     */
    List<TestSuite> batchQueryTestSuites(List<String> ids);
}
