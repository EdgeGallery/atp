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

package org.edgegallery.atp.repository.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.edgegallery.atp.model.task.TaskIdList;
import org.edgegallery.atp.model.task.TaskPO;
import org.edgegallery.atp.model.task.TaskRequest;
import org.edgegallery.atp.repository.mapper.TaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

@Repository
public class TaskRepositoryImpl implements TaskRepository {

    @Autowired
    TaskMapper taskMapper;

    @Override
    public void insert(TaskRequest task) {
        taskMapper.insert(TaskPO.of(task));
    }

    @Override
    public List<TaskRequest> queryAllRunningTasks() {
        return taskMapper.queryAllRunningTasks().stream().map(TaskPO::toDomainModel).collect(Collectors.toList());
    }

    @Override
    public void update(TaskRequest task) {
        taskMapper.update(TaskPO.of(task));
    }

    @Override
    public Date getCurrentDate() {
        return taskMapper.getCurrentDate();
    }

    @Override
    public void delHisTask() {
        taskMapper.delHisTask();
    }

    @Override
    public TaskRequest findByTaskIdAndUserId(String taskId, String userId) {
        return taskMapper.findByTaskIdAndUserId(taskId, userId).toDomainModel();
    }

    @Override
    public List<TaskRequest> findTaskByUserId(String userId, String appName, String status, String providerId,
            String appVersion) {
        List<TaskPO> taskPOList =
                taskMapper.findTaskByUserId(userId, appName, status, providerId, appVersion);
        List<TaskRequest> taskList = new ArrayList<TaskRequest>();
        if (!CollectionUtils.isEmpty(taskPOList)) {
            for (TaskPO task : taskPOList) {
                taskList.add(task.toDomainModel());
            }
        }
        return taskList;
    }

    @Override
    public List<TaskRequest> batchFindTaskByUserId(String userId, TaskIdList taskIdList) {
        List<TaskPO> taskPOList = taskMapper.batchFindTaskByUserId(userId, taskIdList.getTaskIdList());
        List<TaskRequest> taskList = new ArrayList<TaskRequest>();
        if (!CollectionUtils.isEmpty(taskPOList)) {
            for (TaskPO task : taskPOList) {
                taskList.add(task.toDomainModel());
            }
        }
        return taskList;
    }
}
