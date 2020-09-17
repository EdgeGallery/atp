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

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.edgegallery.atp.model.page.Page;
import org.edgegallery.atp.model.page.PageCriteria;
import org.edgegallery.atp.model.task.TaskPO;
import org.edgegallery.atp.model.task.TaskRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TaskRepositoryImpl implements TaskRepository {

	@Autowired
	TaskMapper taskMapper;

	@Override
	public Optional<TaskRequest> find(String taskId) {
		Optional<TaskRequest> task = taskMapper.findByTaskId(taskId).map(TaskPO::toDomainModel);
		return task;

	}

	@Override
	public Page<TaskRequest> queryAll(PageCriteria pageCriteria) {
		long total = taskMapper.countTotal(pageCriteria).longValue();
		List<TaskRequest> releases = taskMapper.findAllWithAppPagination(pageCriteria).stream()
				.map(TaskPO::toDomainModel).collect(Collectors.toList());
		return new Page<>(releases, pageCriteria.getLimit(), pageCriteria.getOffset(), total);
	}

	@Override
	public String generateId() {
		String random = UUID.randomUUID().toString();
		return random.replaceAll("-", "");
	}

	@Override
	public void storeTask(TaskRequest status) {
		taskMapper.store(TaskPO.of(status));
//        if(null != status.getStatus() && status.getSubTaskStatus().length > 0){
//            for(TaskRequest s : status.getSubTaskStatus()) {
//                taskMapper.store(TaskPO.of(s));
//            }
//        }
	}

	@Override
	public List<TaskRequest> queryAllRunningTasks() {
		return taskMapper.queryAllRunningTasks().stream().map(TaskPO::toDomainModel).collect(Collectors.toList());
	}

	@Override
	public List<TaskRequest> queryAllSubTasksByTaskId(String taskId) {
		return taskMapper.queryAllSunTasksByTaskId(taskId).stream().map(TaskPO::toDomainModel)
				.collect(Collectors.toList());
	}
}
