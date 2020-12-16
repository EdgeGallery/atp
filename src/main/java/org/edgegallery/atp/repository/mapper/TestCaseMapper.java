package org.edgegallery.atp.repository.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.edgegallery.atp.model.testcase.TestCase;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface TestCaseMapper {

    TestCase findByTestCaseId(String taskCaseId);

    /**
     * get test case by test case name and type.
     * 
     * @param name name
     * @param type virusScanningTest,complianceTest or sandboxTest
     * @return test case info.
     */
    TestCase findByNameAndType(@Param("name") String name, @Param("type") String type);

    /**
     * get test case by test case name
     * 
     * @param name name
     * @return test case info.
     */
    TestCase findByName(String name);

    /**
     * get all test cases.
     * 
     * @return test case list
     */
    List<TestCase> findAllTestCases(@Param("type") String type, @Param("name") String name,
            @Param("verificationModel") String verificationModel);

    /**
     * find test case by test case className
     * 
     * @param className test case className
     * @return testCase info
     */
    TestCase findByClassName(String className);

    /**
     * find test cases by type.
     * 
     * @param type test case type
     * @return test case info list.
     */
    List<TestCase> findTestCasesByType(@Param("type") String type);

    /**
     * insert into test case table
     * 
     * @param testCase test case info.
     */
    void insert(TestCase testCase);

    /**
     * find one test case by id
     * 
     * @param id id
     * @return test case info
     */
    public TestCase findById(String id);

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
}
