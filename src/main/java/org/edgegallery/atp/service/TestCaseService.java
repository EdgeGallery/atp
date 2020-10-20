package org.edgegallery.atp.service;

import org.edgegallery.atp.model.testcase.TestCaseQueryRes;
import org.springframework.http.ResponseEntity;

public interface TestCaseService {

    /**
     * query all test cases
     * 
     * @return all test cases info.
     */
    public ResponseEntity<TestCaseQueryRes> getAllTestCases();
}
