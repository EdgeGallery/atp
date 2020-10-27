package org.edgegallery.atp.repository.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.edgegallery.atp.model.page.Page;
import org.edgegallery.atp.model.page.PageCriteria;
import org.edgegallery.atp.model.testcase.TestCase;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface TestCaseMapper {

    Page<TestCase> queryAll(PageCriteria pageCriteria);

    Number countTotal(PageCriteria pageCriteria);

    List<TestCase> findAllWithAppPagination(PageCriteria pageCriteria);

    TestCase findByTestCaseId(String taskCaseId);

    void insert(TestCase testCasePO);

    /**
     * get test case by test case name and type.
     * 
     * @param name name
     * @param type virusScanningTest,complianceTest or sandboxTest
     * @return test case info.
     */
    TestCase findByNameAndType(@Param("name") String name, @Param("type") String type);

    /**
     * get all test cases.
     * 
     * @return test case list
     */
    List<TestCase> findAllTestCases();

    /**
     * find test cases by type.
     * 
     * @param type test case type
     * @return test case info list.
     */
    List<TestCase> findTestCasesByType(@Param("type") String type);
}
