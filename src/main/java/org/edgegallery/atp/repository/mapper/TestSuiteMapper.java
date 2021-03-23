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
import org.edgegallery.atp.model.testsuite.TestSuitePo;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface TestSuiteMapper {
    /**
     * create test suite.
     * 
     * @param testSuitePo testSuitePo
     */
    void createTestSuite(TestSuitePo testSuitePo);

    /**
     * get test suite by name.
     * 
     * @param nameCh nameCh
     * @param nameEn nameEn
     * @return TestSuitePo
     */
    TestSuitePo getTestSuiteByName(@Param("nameCh") String nameCh, @Param("nameEn") String nameEn);

    /**
     * get all test suites, name is fuzzy query.
     * 
     * @param nameCh nameCh
     * @param nameEn nameEn
     * @param scenarioId scenario id test suite belongs to
     * @return TestSuitePo list
     */
    List<TestSuitePo> getAllTestSuite(@Param("nameCh") String nameCh, @Param("nameEn") String nameEn,
            @Param("scenarioId") String scenarioId);

    /**
     * update test suite.
     * 
     * @param testSuitePo testSuitePo
     */
    void updateTestSuite(TestSuitePo testSuitePo);
    
    /**
     * get test suite by id.
     * 
     * @param id id
     * @return test suite info
     */
    TestSuitePo getTestSuiteById(String id);

    /**
     * delete test suite by id.
     * 
     * @param id test suite id
     */
    void deleteTestSuite(String id);

    /**
     * batch query test suites.
     * 
     * @param ids test suite ids
     * @return test suite list
     */
    List<TestSuitePo> batchQueryTestSuites(@Param("ids") List<String> ids);
}
