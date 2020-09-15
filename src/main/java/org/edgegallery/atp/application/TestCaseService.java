package org.edgegallery.atp.application;

import org.edgegallery.atp.domain.shared.Page;
import org.edgegallery.atp.domain.shared.PageCriteria;
import org.edgegallery.atp.domain.shared.exceptions.EntityNotFoundException;
import org.edgegallery.atp.model.TestCase;
import org.edgegallery.atp.repository.TestCaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("TestCaseService")
public class TestCaseService {

    @Autowired
    TestCaseRepository testCaseRepository;

    public Page<TestCase> queryAll(PageCriteria pageCriteria) {
        return testCaseRepository.queryAll(pageCriteria);
    }

    public TestCase queryByTestCaseId(String testCaseId) {
        return testCaseRepository.find(testCaseId).orElseThrow(() -> new EntityNotFoundException(TestCase.class, testCaseId));
    }

    public void deleteTestCase(String testCaseId, String userId) {

    }
}
