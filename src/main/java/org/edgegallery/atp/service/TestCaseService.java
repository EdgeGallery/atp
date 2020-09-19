package org.edgegallery.atp.service;

import java.util.List;
import org.edgegallery.atp.model.testcase.TestCase;
import org.edgegallery.atp.model.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface TestCaseService {

    public void testCaseUpload(User user, List<MultipartFile> uploadFiles);

    public ResponseEntity<List<TestCase>> queryAll();

    public ResponseEntity<TestCase> queryByTestCaseId(String testCaseId);

    public void deleteTestCase(String testCaseId, User user);
}
