package org.edgegallery.atp.repository.mapper;

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
    TestScenario getTestScenarioByName(@Param("nameZh") String nameZh, @Param("nameEn") String nameEn);
}
