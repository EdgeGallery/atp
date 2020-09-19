package org.edgegallery.atp.repository.testcase;

import java.util.Optional;
import org.edgegallery.atp.model.page.Page;
import org.edgegallery.atp.model.page.PageCriteria;
import org.edgegallery.atp.model.testcase.TestCase;

public interface TestCaseRepository {

    Page<TestCase> queryAll(PageCriteria pageCriteria);

    Optional<TestCase> find(String taskCaseId);

    Optional<TestCase> findByNameAndType(String name, String type);
}
