package org.edgegallery.atp.service;

import java.util.List;

import org.edgegallery.atp.model.page.Page;
import org.edgegallery.atp.model.page.PageCriteria;
import org.edgegallery.atp.model.testcase.TestCase;
import org.edgegallery.atp.model.user.User;
import org.edgegallery.atp.repository.testcase.TestCaseRepository;
import org.edgegallery.atp.utils.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service("TestCaseService")
public class TestCaseService {

	@Autowired
	TestCaseRepository testCaseRepository;

	public void testCaseUpload(User user, List<MultipartFile> uploadFiles) {

	}

	public ResponseEntity<List<TestCase>> queryAll() {
		return ResponseEntity.ok(queryAll(new PageCriteria(100, 0, "")).getResults());
	}

	public ResponseEntity<TestCase> queryByTestCaseId(String testCaseId) {
		return ResponseEntity.ok(queryById(testCaseId));
	}

	public void deleteTestCase(String testCaseId, User user) {
		deleteTestCase(testCaseId, user.getUserId());
	}

	private Page<TestCase> queryAll(PageCriteria pageCriteria) {
		return testCaseRepository.queryAll(pageCriteria);
	}

	private TestCase queryById(String testCaseId) {
		return testCaseRepository.find(testCaseId)
				.orElseThrow(() -> new EntityNotFoundException(TestCase.class, testCaseId));
	}

	private void deleteTestCase(String testCaseId, String userId) {

	}
}
