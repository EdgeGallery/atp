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

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.edgegallery.atp.model.task.TaskRequest;

public interface TaskRepository {

    /**
     * find task by taskId and userId.
     * 
     * @param taskId taskId
     * @param userId userId
     * @return
     */
    TaskRequest findByTaskIdAndUserId(String taskId, String userId);

    /**
     * find task list by userId.
     * 
     * @param userId userId
     * @return
     */
    List<TaskRequest> findTaskByUserId(String userId, String appName, String status, String providerId,
            String appVersion);

    /**
     * create task info.
     * 
     * @param task task
     */
    void insert(TaskRequest task);

    List<TaskRequest> queryAllRunningTasks();

    /**
     * update task info.
     * 
     * @param task task
     */
    void update(TaskRequest task);

    /**
     * get DB current time.
     * 
     * @return
     */
    Date getCurrentDate();

    /**
     * delete task created 7 days before.
     */
    void delHisTask();

    /**
     * batch delete tasks.
     * 
     * @param ids task id list
     * @return delete failed id list
     */
    Map<String, List<String>> batchDelete(List<String> ids);
}
