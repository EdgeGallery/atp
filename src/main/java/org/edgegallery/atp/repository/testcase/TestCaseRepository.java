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

package org.edgegallery.atp.repository.testcase;

import java.util.List;
import org.edgegallery.atp.model.testcase.TestCase;

public interface TestCaseRepository {

    /**
     * find all test case
     * 
     * @param type manual or automatic
     * @param locale ch or en
     * @param name test case name
     * @param testSuiteIds test suite id list the test case belong to
     * @return test case list
     */
    List<TestCase> findAllTestCases(String type, String locale, String name, String testSuiteId);

    /**
     * find test case by test case name and test case type
     * 
     * @param name test case name
     * @param type test case type
     * @return
     */
    TestCase findByNameAndType(String name, String type);

    /**
     * find test case by test case className
     * 
     * @param className test case className
     * @return testCase info
     */
    TestCase findByClassName(String className);

    /**
     * insert into test case table
     * 
     * @param testCase test case info.
     */
    void insert(TestCase testCase);

    /**
     * update test case
     * 
     * @param testCase test case info
     * @return test case info
     */
    public void update(TestCase testCase);

    /**
     * delete test case by test case id
     * 
     * @param id test case id
     * @return operation complete
     */
    public int delete(String id);

    /**
     * get specific test case by test case id.
     * 
     * @param id test case id
     * @return test case info
     */
    public TestCase getTestCaseById(String id);
    
    /**
     * get test case by test case name
     * 
     * @param nameCh chinese name
     * @param nameEn english name
     * @return test case info.
     */
    public TestCase findByName(String nameCh, String nameEn);

}
