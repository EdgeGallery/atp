package org.edgegallery.atp.service;

import java.util.List;
import org.edgegallery.atp.model.testcase.TestCase;
import org.springframework.http.ResponseEntity;

public interface TestCaseService {

    /**
     * query all test cases
     * 
     * @param type test case type
     * @return all test cases info.
     */
    public ResponseEntity<List<TestCase>> getAllTestCases(String type);
}
