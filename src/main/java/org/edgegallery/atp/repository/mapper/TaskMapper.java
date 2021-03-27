/*
 * Copyright 2020-2021 Huawei Technologies Co., Ltd.
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

package org.edgegallery.atp.repository.mapper;

import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.edgegallery.atp.model.task.TaskPO;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface TaskMapper {

    /**
     * get task info by taskId and userId.
     * 
     * @param taskId taskId
     * @return taskInfo
     */
    TaskPO findByTaskIdAndUserId(@Param("taskId") String taskId, @Param("userId") String userId);

    /**
     * get task list by userId.
     * 
     * @param userId userId
     * @return TaskPO
     */
    List<TaskPO> findTaskByUserId(@Param("userId") String userId, @Param("appName") String appName,
            @Param("status") String status, @Param("providerId") String providerId,
            @Param("appVersion") String appVersion);

    /**
     * create task info.
     * 
     * @param taskPO taskPO
     */
    void insert(TaskPO taskPO);

    /**
     * query all running tasks.
     * 
     * @return running task info list
     */
    List<TaskPO> queryAllRunningTasks();

    /**
     * update task info.
     * 
     * @param taskPO taskPO
     */
    void update(TaskPO taskPO);

    /**
     * get current db time.
     * 
     * @return current time
     */
    Date getCurrentDate();

    /**
     * delete task created 30 days before.
     */
    void delHisTask();

    /**
     * batch get task.
     * 
     * @param userId userId
     * @param taskIdList taskIdList
     * @return taskInfo List
     */
    List<TaskPO> batchFindTaskByUserId(@Param("userId") String userId, @Param("taskIdList") List<String> taskIdList);

    /**
     * delete task by id.
     * 
     * @param id id
     */
    void deleteTaskById(String id);
}
