package org.edgegallery.atp.service;

import java.util.List;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.model.testcase.TestCase;
import org.edgegallery.atp.repository.testcase.TestCaseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service("TestCaseService")
public class TestCaseServiceImpl implements TestCaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestCaseServiceImpl.class);

    @Autowired
    TestCaseRepository testCaseRepository;

    @Override
    public ResponseEntity<List<TestCase>> getAllTestCases(String type) {
        if (Constant.COMPLIANCE_TEST.equals(type) || Constant.SANDBOX_TEST.equals(type)
                || Constant.VIRUS_SCAN_TEST.equals(type)) {
            return ResponseEntity.ok(testCaseRepository.findTestCasesByType(type));
        }

        List<TestCase> response = testCaseRepository.findAllTestCases();
        LOGGER.info("get all test cases successfully.");
        return ResponseEntity.ok(response);
    }
}
