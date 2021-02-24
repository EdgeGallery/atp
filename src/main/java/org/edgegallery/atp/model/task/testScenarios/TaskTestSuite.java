package org.edgegallery.atp.model.task.testScenarios;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskTestSuite {
    /**
     * test suite id
     */
    String id;

    /**
     * test suite chinese name
     */
    String nameCh;

    /**
     * test suite english name
     */
    String nameEn;

    /**
     * test case the test suite contains
     */
    List<TaskTestCase> testCases;
}
