package org.edgegallery.atp.repository.testcase;

import org.apache.ibatis.annotations.Mapper;
import org.edgegallery.atp.model.page.Page;
import org.edgegallery.atp.model.page.PageCriteria;
import org.edgegallery.atp.model.testcase.TestCase;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Mapper
public interface TestCaseMapper {

    Page<TestCase> queryAll(PageCriteria pageCriteria);

    Number countTotal(PageCriteria pageCriteria);

    List<TestCasePO> findAllWithAppPagination(PageCriteria pageCriteria);

    List<TestCasePO> findAll();

    Optional<TestCasePO> findByTestCaseId(String taskCaseId);

    void insert(TestCasePO testCasePO);
}
