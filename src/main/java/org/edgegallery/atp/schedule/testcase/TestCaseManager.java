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

package org.edgegallery.atp.schedule.testcase;

import org.edgegallery.atp.model.task.TaskRequest;

/**
 * test case schedule class.
 *
 */
public interface TestCaseManager {
    /**
     * execute test case asynchronously
     * 
     * @param task test task
     * @param filePath scar file path
     */
    void executeTestCase(TaskRequest task, String filePath);
}
