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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.edgegallery.atp.model.task.TaskPO;
import org.edgegallery.atp.model.task.TaskRequest;
import org.edgegallery.atp.repository.mapper.TaskMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

@Repository
public class TaskRepositoryImpl implements TaskRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskRepositoryImpl.class);

    @Autowired
    TaskMapper taskMapper;

    @Override
    public void insert(TaskRequest task) {
        try {
            taskMapper.insert(TaskPO.of(task));
        } catch (Exception e) {
            LOGGER.error("insert task failed. {}", e);
            throw new IllegalArgumentException("insert task failed.");
        }
    }

    @Override
    public List<TaskRequest> queryAllRunningTasks() {
        try {
            return taskMapper.queryAllRunningTasks().stream().map(TaskPO::toDomainModel).collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.error("queryAllRunningTasks failed. {}", e);
            throw new IllegalArgumentException("queryAllRunningTasks failed.");
        }

    }

    @Override
    public void update(TaskRequest task) {
        try {
            taskMapper.update(TaskPO.of(task));
        } catch (Exception e) {
            LOGGER.error("update failed. {}", e);
            throw new IllegalArgumentException("update failed.");
        }
    }

    @Override
    public Date getCurrentDate() {
        try {
            return taskMapper.getCurrentDate();
        } catch (Exception e) {
            LOGGER.error("getCurrentDate failed. {}", e);
            throw new IllegalArgumentException("getCurrentDate failed.");
        }
    }

    @Override
    public void delHisTask() {
        try {
            taskMapper.delHisTask();
        } catch (Exception e) {
            LOGGER.error("delHisTask failed. {}", e);
            throw new IllegalArgumentException("delHisTask failed.");
        }
    }

    @Override
    public TaskRequest findByTaskIdAndUserId(String taskId, String userId) {
        try {
            return null != taskMapper.findByTaskIdAndUserId(taskId, userId)
                    ? taskMapper.findByTaskIdAndUserId(taskId, userId).toDomainModel()
                    : null;
        } catch (Exception e) {
            LOGGER.error("findByTaskIdAndUserId failed. {}", e);
            throw new IllegalArgumentException("findByTaskIdAndUserId failed.");
        }
    }

    @Override
    public List<TaskRequest> findTaskByUserId(String userId, String appName, String status, String providerId,
            String appVersion) {
        try {
            List<TaskPO> taskPoList = taskMapper.findTaskByUserId(userId, appName, status, providerId, appVersion);
            return CollectionUtils.isEmpty(taskPoList) ? new ArrayList<TaskRequest>()
                    : taskPoList.stream().map(taskPo -> taskPo.toDomainModel()).collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.error("findTaskByUserId failed. {}", e);
            throw new IllegalArgumentException("findTaskByUserId failed.");
        }
    }

    @Override
    public Map<String, List<String>> batchDelete(List<String> ids) {
        Map<String, List<String>> result = new HashMap<String, List<String>>();
        List<String> failIds = new ArrayList<String>();
        for (String id : ids) {
            try {
                taskMapper.deleteTaskById(id);
            } catch (Exception e) {
                LOGGER.error("delete task by id {} failed. {}", id, e);
                failIds.add(id);
            }
        }

        result.put("failed", failIds);
        return result;
    }
}
