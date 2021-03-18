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
package org.edgegallery.atp.service;

import java.io.FileNotFoundException;
import java.util.List;
import org.edgegallery.atp.model.testsuite.TestSuite;

public interface TestSuiteService {
    /**
     * create test suite
     * 
     * @param testSuite test suite info
     * @return test suite info
     */
    TestSuite createTestSuite(TestSuite testSuite);

    /**
     * update test suite
     * 
     * @param testSuite test suite info
     * @return test suite info
     */
    TestSuite updateTestSuite(TestSuite testSuite);

    /**
     * delete test suite by id
     * 
     * @param id test suite id
     * @return true
     */
    Boolean deleteTestSuite(String id);

    /**
     * get test suite by id
     * 
     * @param id test suite id
     * @return test suite info
     */
    TestSuite getTestSuite(String id) throws FileNotFoundException;

    /**
     * get all test suites
     * 
     * @param locale locale language
     * @param name test case name
     * @param scenarioIdList scenario id list test suite belongs to
     * @return test suite list
     */
    List<TestSuite> queryAllTestSuite(String locale, String name, List<String> scenarioIdList);
}
