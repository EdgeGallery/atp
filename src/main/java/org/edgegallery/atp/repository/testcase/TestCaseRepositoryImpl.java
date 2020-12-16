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

package org.edgegallery.atp.repository.testcase;

import java.util.List;
import org.edgegallery.atp.model.testcase.TestCase;
import org.edgegallery.atp.repository.mapper.TestCaseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TestCaseRepositoryImpl implements TestCaseRepository {

    @Autowired
    private TestCaseMapper testCaseMapper;

    @Override
    public List<TestCase> findAllTestCases(String type, String name, String verificationModel) {
        return testCaseMapper.findAllTestCases(type, name, verificationModel);
    }

    @Override
    public TestCase findByNameAndType(String name, String type) {
        return testCaseMapper.findByNameAndType(name, type);
    }

    @Override
    public void insert(TestCase testCase) {
        testCaseMapper.insert(testCase);
    }

    @Override
    public void update(TestCase testCase) {
        testCaseMapper.update(testCase);
    }

    @Override
    public int delete(String id) {
        return testCaseMapper.delete(id);
    }

    @Override
    public TestCase getTestCaseById(String id) {
        return testCaseMapper.getTestCaseById(id);
    }

    @Override
    public TestCase findByName(String name) {
        return testCaseMapper.findByName(name);
    }

    @Override
    public TestCase findByClassName(String className) {
        return testCaseMapper.findByClassName(className);
    }
}
