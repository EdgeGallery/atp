package org.edgegallery.atp.service;

import java.util.List;
import org.edgegallery.atp.model.testcase.TestCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface TestCaseService {

    /**
     * query all test cases
     * 
     * @param type test case type
     * @return all test cases info.
     */
    public ResponseEntity<List<TestCase>> getAllTestCases(String type, String name, String verificationModel);

    /**
     * create test cases
     * 
     * @param file test case file
     * @param testCase test case info
     * @return test case info
     */
    public TestCase createTestCase(MultipartFile file, TestCase testCase);

    /**
     * update test case
     * 
     * @param file test case file
     * @param testCase test case info
     * @return test case info
     */
    public TestCase updateTestCase(MultipartFile file, TestCase testCase);

    /**
     * delete test case
     * 
     * @param id id
     * @return if delete successa
     */
    public Boolean deleteTestCase(String id);

    /**
     * get one test case
     * 
     * @param id id
     * @return test case info
     */
    public TestCase getTestCase(String id);
}
