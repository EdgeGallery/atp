package org.edgegallery.atp.repository.testcase;

import org.edgegallery.atp.model.page.Page;
import org.edgegallery.atp.model.page.PageCriteria;
import org.edgegallery.atp.model.testcase.TestCase;

import java.util.Optional;

public interface TestCaseRepository {

    Page<TestCase> queryAll(PageCriteria pageCriteria);

    Optional<TestCase> find(String taskCaseId);
}
