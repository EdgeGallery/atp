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
     * create test suite
     * 
     * @param TestSuitePo TestSuitePo
     */
    void createTestSuite(TestSuitePo testSuitePo);

    /**
     * get test suite by name
     * 
     * @param name
     * @return
     */
    TestSuitePo getTestSuiteByName(@Param("nameCh") String nameCh, @Param("nameEn") String nameEn);

    /**
     * get all test suites, name is fuzzy query
     * 
     * @param nameCh
     * @param nameEn
     * @param scenarioId scenario id test suite belongs to
     * @return
     */
    List<TestSuitePo> getAllTestSuite(@Param("nameCh") String nameCh, @Param("nameEn") String nameEn,
            @Param("scenarioId") String scenarioId);

    /**
     * update test suite
     * 
     * @param TestSuitePo TestSuitePo
     * @return TestSuitePo
     */
    void updateTestSuite(TestSuitePo testSuitePo);
    
    /**
     * get test suite by id
     * 
     * @param id id
     * @return test suite info
     */
    TestSuitePo getTestSuiteById(String id);

    /**
     * delete test suite by id
     * 
     * @param id test suite id
     */
    void deleteTestSuite(String id);

    /**
     * batch query test suites
     * 
     * @param ids test suite ids
     * @return test suite list
     */
    List<TestSuitePo> batchQueryTestSuites(@Param("ids") List<String> ids);
}
