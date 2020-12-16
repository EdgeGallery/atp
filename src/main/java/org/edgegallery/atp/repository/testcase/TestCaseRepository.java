package org.edgegallery.atp.repository.testcase;

import java.util.List;
import org.edgegallery.atp.model.testcase.TestCase;

public interface TestCaseRepository {

    /**
     * find all test case
     * 
     * @return
     */
    List<TestCase> findAllTestCases(String type, String name, String verificationModel);

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
     * @param name name
     * @return test case info.
     */
    public TestCase findByName(String name);
}
