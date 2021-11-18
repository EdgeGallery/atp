/*
 * Copyright 2021 Huawei Technologies Co., Ltd.
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

package org.edgegallery.atp.model.testscenario;

import com.alibaba.fastjson.JSONObject;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.edgegallery.atp.model.testcase.TestCase;
import org.edgegallery.atp.model.testsuite.TestSuite;

@Getter
@Setter
public class TestModelBatchImport {

    /**
     * test scenario list.
     */
    private List<TestScenario> testScenarioList = new ArrayList<TestScenario>();

    /**
     * test suite list.
     */
    private List<TestSuite> testSuiteList = new ArrayList<TestSuite>();

    /**
     * test case list.
     */
    private List<TestCase> testCaseList = new ArrayList<TestCase>();

    /**
     * test case file, key: file name, value: file.
     */
    private Map<String, File> testCaseFile = new HashMap<String, File>();

    /**
     * scenario icon file.
     */
    private Map<String, File> scenarioIconFile = new HashMap<String, File>();

    /**
     * failure objects.
     */
    private List<JSONObject> failures = new ArrayList<JSONObject>();

    /**
     * failure object id list.
     */
    private Set<String> failureIds = new HashSet<String>();

}
