package org.edgegallery.atp.model.task;

import java.io.IOException;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.edgegallery.atp.model.testcase.TestCaseDetail;
import org.edgegallery.atp.model.user.User;
import org.edgegallery.atp.repository.PersistenceObject;
import org.edgegallery.atp.utils.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskPO implements PersistenceObject<TaskRequest> {

	@Column(name = "id")
	private String id;

	@Column(name = "appName")
	private String appName;

	@Column(name = "appVersion")
	private String appVersion;

	@Column(name = "status")
	private String status;

	@Column(name = "createTime")
	private String createTime;

	@Column(name = "endTime")
	private String endTime;

	@Column(name = "userId")
	private String userId;

	@Column(name = "userName")
	private String userName;

	@Column(name = "testCaseDetail")
	private String testCaseDetail;

	public TaskPO() {

	}

	public static TaskPO of(TaskRequest startTest) {
		TaskPO build = new TaskPO();
		return build;
	}

	@Override
	public TaskRequest toDomainModel() {
		Logger logger = LoggerFactory.getLogger(TaskPO.class);
		try {
			return TaskRequest.builder().setAppName(appName).setAppVersion(appVersion).setCreateTime(createTime)
					.setEndTime(endTime).setId(id).setStatus(status)
					.setTestCaseDetail(JSONUtil.unMarshal(testCaseDetail, TestCaseDetail.class))
					.setUser(new User(userId, userName)).build();
		} catch (IOException e) {
			logger.error("taskPO JSONUtil unmarshal testcaseDetail failed. {}", testCaseDetail);
			return new TaskRequest();
		}

	}
}
