package org.edgegallery.atp.service;

import java.util.List;
import org.edgegallery.atp.model.testcase.TestCase;
import org.edgegallery.atp.model.user.User;
import org.edgegallery.atp.repository.testcase.TestCaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service("TestCaseService")
public class TestCaseServiceImpl implements TestCaseService {

    @Autowired
    TestCaseRepository testCaseRepository;

    @Override
    public void testCaseUpload(User user, List<MultipartFile> uploadFiles) {

    }

    @Override
    public ResponseEntity<TestCase> queryByTestCaseId(String testCaseId) {
        // TODO
        return ResponseEntity.ok(null);
    }

    @Override
    public void deleteTestCase(String testCaseId, User user) {
        deleteTestCase(testCaseId, user.getUserId());
    }

    private void deleteTestCase(String testCaseId, String userId) {

    }
}
