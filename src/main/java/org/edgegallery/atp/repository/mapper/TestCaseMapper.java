/*
 * Copyright 2020-2021 Huawei Technologies Co., Ltd.
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
import org.edgegallery.atp.model.testcase.TestCasePo;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface TestCaseMapper {

    TestCasePo findByTestCaseId(String taskCaseId);

    /**
     * get test case by test case name and type.
     *
     * @param name name
     * @param type virusScanningTest,complianceTest or sandboxTest
     * @return test case info.
     */
    TestCasePo findByNameAndType(@Param("name") String name, @Param("type") String type);

    /**
     * get test case by test case name.
     *
     * @param nameCh chinese name
     * @param nameEn english name
     * @return test case info.
     */
    TestCasePo findByName(@Param("nameCh") String nameCh, @Param("nameEn") String nameEn);

    /**
     * get all test cases.
     *
     * @return test case list
     */
    List<TestCasePo> findAllTestCases(@Param("type") String type, @Param("nameCh") String nameCh,
        @Param("nameEn") String nameEn, @Param("testSuiteId") String testSuiteId);

    /**
     * find all test cases.
     *
     * @param type manual or automatic
     * @param nameCh nameCh
     * @param nameEn nameEn
     * @param testSuiteId test suite id the test case belong to
     * @param limit limit
     * @param offset offset
     * @return test case list
     */
    List<TestCasePo> findAllTestCasesByPaginition(@Param("type") String type, @Param("nameCh") String nameCh,
        @Param("nameEn") String nameEn, @Param("testSuiteId") String testSuiteId, @Param("limit") int limit,
        @Param("offset") int offset);

    /**
     * get test case count.
     *
     * @param type manual or automatic
     * @param nameCh nameCh
     * @param nameEn nameEn
     * @param testSuiteId test suite id the test case belong to
     * @return test case count
     */
    int countTotal(@Param("type") String type, @Param("nameCh") String nameCh, @Param("nameEn") String nameEn,
        @Param("testSuiteId") String testSuiteId);

    /**
     * find test case by test case className.
     *
     * @param className test case className
     * @return testCase info
     */
    TestCasePo findByClassName(String className);

    /**
     * find test cases by type.
     *
     * @param type test case type
     * @return test case info list.
     */
    List<TestCasePo> findTestCasesByType(@Param("type") String type);

    /**
     * insert into test case table.
     *
     * @param testCase test case info.
     */
    void insert(TestCasePo testCase);

    /**
     * find one test case by id.
     *
     * @param id id
     * @return test case info
     */
    TestCasePo findById(String id);

    /**
     * update test case.
     *
     * @param testCase test case info
     */
    void update(TestCasePo testCase);

    /**
     * delete test case by test case id.
     *
     * @param id test case id
     * @return operation complete
     */
    int delete(String id);

    /**
     * get specific test case by test case id.
     *
     * @param id test case id
     * @return test case info
     */
    TestCasePo getTestCaseById(String id);

    /**
     * find by config id.
     *
     * @param configIdList configIdList
     * @return TestCasePo
     */
    List<TestCasePo> findByConfigId(String configIdList);
}
