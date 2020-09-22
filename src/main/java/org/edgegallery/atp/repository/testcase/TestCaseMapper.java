package org.edgegallery.atp.repository.testcase;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
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
    TestCase findByNameAndType(String name, String type);

    /**
     * get all test cases.
     * 
     * @return test case list
     */
    List<TestCase> findAllTestCases();
}
