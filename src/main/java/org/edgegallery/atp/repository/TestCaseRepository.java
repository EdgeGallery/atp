package org.edgegallery.atp.repository;

import org.edgegallery.atp.domain.shared.Page;
import org.edgegallery.atp.domain.shared.PageCriteria;
import org.edgegallery.atp.model.TestCase;

import java.util.Optional;

public interface TestCaseRepository {

    Page<TestCase> queryAll(PageCriteria pageCriteria);

    Optional<TestCase> find(String taskCaseId);
}
