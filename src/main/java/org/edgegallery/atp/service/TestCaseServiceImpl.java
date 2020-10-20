package org.edgegallery.atp.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.model.testcase.TestCase;
import org.edgegallery.atp.model.testcase.TestCaseQueryRes;
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

    private final String NAME = "name";

    private final String DESCRIPTION = "description";

    @Override
    public ResponseEntity<TestCaseQueryRes> getAllTestCases() {
        List<TestCase> testCaseList = testCaseRepository.findAllTestCases();
        List<Map<String, String>> virusScanningTest = new ArrayList<Map<String, String>>();
        List<Map<String, String>> complianceTest = new ArrayList<Map<String, String>>();
        List<Map<String, String>> sandboxTest = new ArrayList<Map<String, String>>();

        testCaseList.forEach(testCase -> {
            Map<String, String> testCaseMap = new HashMap<String, String>();
            switch (testCase.getType()) {
                case Constant.testCaseType.COMPLIANCE_TEST:
                    testCaseMap.put(NAME, testCase.getName());
                    testCaseMap.put(DESCRIPTION, testCase.getDescription());
                    complianceTest.add(testCaseMap);
                    break;
                case Constant.testCaseType.SANDBOX_TEST:
                    testCaseMap.put(NAME, testCase.getName());
                    testCaseMap.put(DESCRIPTION, testCase.getDescription());
                    sandboxTest.add(testCaseMap);
                    break;
                case Constant.testCaseType.VIRUS_SCAN_TEST:
                    testCaseMap.put(NAME, testCase.getName());
                    testCaseMap.put(DESCRIPTION, testCase.getDescription());
                    virusScanningTest.add(testCaseMap);
                    break;
                default:
                    break;
            }
        });

        TestCaseQueryRes resBody = new TestCaseQueryRes(virusScanningTest, complianceTest, sandboxTest);
        return ResponseEntity.ok(resBody);
    }
}
