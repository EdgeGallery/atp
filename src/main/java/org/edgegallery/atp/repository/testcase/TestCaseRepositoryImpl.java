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
import java.util.stream.Collectors;
import org.edgegallery.atp.model.page.Page;
import org.edgegallery.atp.model.page.PageCriteria;
import org.edgegallery.atp.model.testcase.TestCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TestCaseRepositoryImpl implements TestCaseRepository {

    @Autowired
    private TestCaseMapper testCaseMapper;

    @Override
    public Page<TestCase> queryAll(PageCriteria pageCriteria) {
        long total = testCaseMapper.countTotal(pageCriteria).longValue();
        List<TestCase> releases =
                testCaseMapper.findAllWithAppPagination(pageCriteria).stream().collect(Collectors.toList());
        return new Page<>(releases, pageCriteria.getLimit(), pageCriteria.getOffset(), total);
    }

    @Override
    public List<TestCase> findAllTestCases() {
        return testCaseMapper.findAllTestCases();
    }

    @Override
    public TestCase findByNameAndType(String name, String type) {
        return testCaseMapper.findByNameAndType(name, type);
    }
}