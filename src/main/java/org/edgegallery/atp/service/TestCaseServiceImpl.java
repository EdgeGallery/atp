package org.edgegallery.atp.service;

import java.util.List;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.model.testcase.TestCase;
import org.edgegallery.atp.repository.testcase.TestCaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service("TestCaseService")
public class TestCaseServiceImpl implements TestCaseService {

    @Autowired
    TestCaseRepository testCaseRepository;

    @Override
    public ResponseEntity<List<TestCase>> getAllTestCases(String type) {
        if (Constant.COMPLIANCE_TEST.equals(type) || Constant.SANDBOX_TEST.equals(type)
                || Constant.VIRUS_SCAN_TEST.equals(type)) {
            return ResponseEntity.ok(testCaseRepository.findTestCasesByType(type));
        }

        return ResponseEntity.ok(testCaseRepository.findAllTestCases());
    }
}
