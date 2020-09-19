package org.edgegallery.atp.repository.testcase;

import java.util.List;
import java.util.Optional;
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

    List<TestCase> findAll();

    Optional<TestCase> findByTestCaseId(String taskCaseId);

    void insert(TestCase testCasePO);

    Optional<TestCase> findByNameAndType(String name, String type);
}
