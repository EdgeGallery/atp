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
     * create test scenario
     * 
     * @param testScenario testScenario
     */
    void createTestScenario(TestScenario testScenario);

    /**
     * get test scenario by name
     * 
     * @param name
     * @return
     */
    TestScenario getTestScenarioByName(@Param("nameCh") String nameCh, @Param("nameEn") String nameEn);

    /**
     * get all test scenarios, name is fuzzy query
     * 
     * @param nameCh
     * @param nameEn
     * @return
     */
    List<TestScenario> getAllTestScenario(@Param("nameCh") String nameCh, @Param("nameEn") String nameEn);

    /**
     * update test scenario
     * 
     * @param testScenario testScenario
     * @return testScenario
     */
    void updateTestScenario(TestScenario testScenario);
    
    /**
     * get test scenario by id
     * @param id id
     * @return test scenario info
     */
    TestScenario getTestScenarioById(String id);

    /**
     * delete test scenario by id
     * 
     * @param id test scenario id
     */
    void deleteTestScenario(String id);

}
