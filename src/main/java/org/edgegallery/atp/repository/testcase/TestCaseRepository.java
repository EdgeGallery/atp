package org.edgegallery.atp.repository.testcase;

import java.util.List;
import org.edgegallery.atp.model.page.Page;
import org.edgegallery.atp.model.page.PageCriteria;
import org.edgegallery.atp.model.testcase.TestCase;

public interface TestCaseRepository {

    Page<TestCase> queryAll(PageCriteria pageCriteria);

    List<TestCase> findAllTestCases();

    TestCase findByNameAndType(String name, String type);
}
