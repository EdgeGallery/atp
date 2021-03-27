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

package org.edgegallery.atp.model.task.testscenarios;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskTestScenario {
    /**
     * test scenario id.
     */
    String id;

    /**
     * test scenario chinese name.
     */
    String nameCh;

    /**
     * test scenario english name.
     */
    String nameEn;

    /**
     * test scenario label.
     */
    String label;

    /**
     * test suite list the test scenario contains.
     */
    List<TaskTestSuite> testSuites;

    public TaskTestScenario() {

    }

    public TaskTestScenario(TaskTestScenarioPo taskPo) {
        this.id = taskPo.getId();
    }

    /**
     * TaskTestScenario model to TaskTestScenarioPo model.
     * 
     * @return TaskTestScenarioPo
     */
    public TaskTestScenarioPo of() {
        TaskTestScenarioPo taskPo = new TaskTestScenarioPo();
        taskPo.setId(this.getId());
        return taskPo;
    }
}
