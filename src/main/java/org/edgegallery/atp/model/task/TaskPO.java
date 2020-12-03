package org.edgegallery.atp.model.task;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import org.edgegallery.atp.model.testcase.TestCaseDetail;
import org.edgegallery.atp.model.user.User;
import org.edgegallery.atp.repository.PersistenceObject;
import org.edgegallery.atp.utils.JSONUtil;
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
    private Date createTime;

    @Column(name = "endTime")
    private Date endTime;

    @Column(name = "userId")
    private String userId;

    @Column(name = "userName")
    private String userName;

    @Column(name = "testCaseDetail")
    private String testCaseDetail;

    @Column(name = "providerId")
    private String providerId;
    
    @Column(name = "packagePath")
    private String packagePath;
    
    public static TaskPO of(TaskRequest startTest) {
        TaskPO taskPO = new TaskPO();
        taskPO.setAppName(startTest.getAppName());
        taskPO.setAppVersion(startTest.getAppVersion());
        taskPO.setCreateTime(startTest.getCreateTime());
        taskPO.setEndTime(startTest.getEndTime());
        taskPO.setId(startTest.getId());
        taskPO.setStatus(startTest.getStatus());
        taskPO.setUserId(startTest.getUser().getUserId());
        taskPO.setUserName(startTest.getUser().getUserName());
        taskPO.setTestCaseDetail(JSONUtil.marshal(startTest.getTestCaseDetail()));
        taskPO.setPackagePath(startTest.getPackagePath());
        taskPO.setProviderId(startTest.getProviderId());

        return taskPO;
    }

    @Override
    public TaskRequest toDomainModel() {
        return TaskRequest.builder().setAppName(appName).setAppVersion(appVersion).setCreateTime(createTime)
                .setEndTime(endTime).setPackagePath(packagePath).setProviderId(providerId).setId(id).setStatus(status)
                .setTestCaseDetail(JSONUtil.unMarshal(testCaseDetail, TestCaseDetail.class))
                .setUser(new User(userId, userName)).build();

    }
}
