package org.edgegallery.atp.repository.testcase;

import java.util.List;
import org.edgegallery.atp.model.testcase.TestCase;

public interface TestCaseRepository {

    /**
     * find all test case
     * 
     * @return
     */
    List<TestCase> findAllTestCases();

    /**
     * find test case by test case name and test case type
     * 
     * @param name test case name
     * @param type test case type
     * @return
     */
    TestCase findByNameAndType(String name, String type);

    /**
     * 
     * @param type test case type
     * @return
     */
    List<TestCase> findTestCasesByType(String type);
}
