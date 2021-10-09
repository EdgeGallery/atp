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

package org.edgegallery.atp.repositoryTest;

import java.util.UUID;
import org.edgegallery.atp.ATPApplicationTest;
import org.edgegallery.atp.model.task.TaskRequest;
import org.edgegallery.atp.repository.task.TaskRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ATPApplicationTest.class)
@AutoConfigureMockMvc
public class TaskRepositoryExceptionTest {
    @Autowired
    TaskRepository taskRepository;

    @Test(expected = Exception.class)
    public void insertException() {
        TaskRequest task = new TaskRequest();
        String id = UUID.randomUUID().toString();
        task.setId(id);
        taskRepository.insert(task);
        //construct duplicate key error
        taskRepository.insert(task);
    }

    @Test(expected = Exception.class)
    public void updateException() {
        TaskRequest task = new TaskRequest();
        String id = UUID.randomUUID().toString();
        task.setId(id);
        taskRepository.update(task);
    }
}