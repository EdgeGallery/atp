package org.edgegallery.atp.domain.model.testcase;

import org.edgegallery.atp.domain.shared.Page;
import org.edgegallery.atp.domain.shared.PageCriteria;

import java.util.Optional;

public interface TestCaseRepository {

    Page<TestCase> queryAll(PageCriteria pageCriteria);

    Optional<TestCase> find(String taskCaseId);
}
