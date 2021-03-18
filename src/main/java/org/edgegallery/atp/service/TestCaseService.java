/*
 * Copyright 2020 Huawei Technologies Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.edgegallery.atp.service;

import java.io.FileNotFoundException;
import java.util.List;
import org.edgegallery.atp.model.testcase.TestCase;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface TestCaseService {

    /**
     * query all test cases
     * 
     * @param type manual or automatic
     * @param locale ch or zh
     * @param name test case name
     * @param testSuiteIds test suite id list the test case belong to
     * @return test case info list
     */
    public ResponseEntity<List<TestCase>> getAllTestCases(String type, String locale, String name,
            List<String> testSuiteIds);

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
    public TestCase getTestCase(String id) throws FileNotFoundException;

    /**
     * download test case.
     * 
     * @param id test case id
     * @return test case binary stream.
     */
    public ResponseEntity<InputStreamResource> downloadTestCase(String id);
}
