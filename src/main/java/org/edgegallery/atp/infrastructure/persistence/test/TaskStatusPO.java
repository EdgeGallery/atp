package org.edgegallery.atp.infrastructure.persistence.test;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.edgegallery.atp.domain.model.test.TaskStatus;
import org.edgegallery.atp.domain.model.user.User;
import org.edgegallery.atp.infrastructure.persistence.PersistenceObject;

import javax.persistence.Column;
import javax.persistence.Entity;

@Getter
@Setter
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskStatusPO implements PersistenceObject<TaskStatus> {

    @Column(name = "ID")
    private String id;

    @Column(name = "TESTCASENAME")
    private String testCaseName;

    @Column(name = "STARTTIME")
    private String startTime;

    @Column(name = "ENDTIME")
    private String endTime;

    @Column(name = "TESTRESULT")
    private String testResult;

    @Column(name = "USERID")
    private String userId;

    @Column(name = "USERNAME")
    private String userName;

    @Column(name = "DESC")
    private String desc;

    private TaskStatusPO[] subTaskStatus;

    public TaskStatusPO() {

    }

    public static TaskStatusPO of(TaskStatus startTest) {
        TaskStatusPO build = new TaskStatusPO();
        return build;
    }

    @Override
    public TaskStatus toDomainModel() {
        return TaskStatus.builder()
                .setEndTime(endTime)
                .setStartTime(startTime)
                .setTestCaseName(testCaseName)
                .setUser(new User(userId, userName))
                .setId(id)
                .setStatus(testResult)
                .setTestCaseDesc(desc)
                .build();
    }
}
