package org.edgegallery.atp.model.task.testScenarios;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskTestScenario {
    /**
     * test scenario id
     */
    String id;
    
    /**
     * test scenario chinese name
     */
    String nameCh;
    
    /**
     * test scenario english name
     */
    String nameEn;
    
    /**
     * test suite list the test scenario contains
     */
    List<TaskTestSuite> testSuites;
}
