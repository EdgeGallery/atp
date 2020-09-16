/*
 *    Copyright 2020 Huawei Technologies Co., Ltd.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.edgegallery.atp.repository.task;

import org.edgegallery.atp.model.page.Page;
import org.edgegallery.atp.model.page.PageCriteria;
import org.edgegallery.atp.model.task.TaskStatus;
import org.edgegallery.atp.model.task.TaskStatusPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class TaskRepositoryImpl implements TaskRepository {

    @Autowired
    TaskMapper taskMapper;

    @Override
    public Optional<TaskStatus> find(String taskId) {
        Optional<TaskStatus> task = taskMapper.findByTaskId(taskId).map(TaskStatusPO::toDomainModel);
        return task;

    }

    @Override
    public Page<TaskStatus> queryAll(PageCriteria pageCriteria) {
        long total = taskMapper.countTotal(pageCriteria).longValue();
        List<TaskStatus> releases = taskMapper.findAllWithAppPagination(pageCriteria)
                .stream()
                .map(TaskStatusPO::toDomainModel)
                .collect(Collectors.toList());
        return new Page<>(releases, pageCriteria.getLimit(), pageCriteria.getOffset(), total);
    }

    @Override
    public String generateId() {
        String random = UUID.randomUUID().toString();
        return random.replaceAll("-", "");
    }

    @Override
    public void storeTask(TaskStatus status) {
        taskMapper.store(TaskStatusPO.of(status));
        if(null != status.getStatus() && status.getSubTaskStatus().length > 0){
            for(TaskStatus s : status.getSubTaskStatus()) {
                taskMapper.store(TaskStatusPO.of(s));
            }
        }
    }

    @Override
    public List<TaskStatus> queryAllRunningTasks() {
        return taskMapper.queryAllRunningTasks()
                .stream()
                .map(TaskStatusPO::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskStatus> queryAllSubTasksByTaskId(String taskId) {
        return taskMapper.queryAllSunTasksByTaskId(taskId)
                .stream()
                .map(TaskStatusPO::toDomainModel)
                .collect(Collectors.toList());
    }
}
