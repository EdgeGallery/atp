package org.edgegallery.atp.infrastructure.persistence.test;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.edgegallery.atp.domain.model.test.TaskStatus;
import org.edgegallery.atp.infrastructure.persistence.PersistenceObject;

import javax.persistence.Entity;

@Getter
@Setter
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskStatusPO implements PersistenceObject<TaskStatus> {

    private String id;

    private String desc;

    private String startTime;

    private String endTime;

    private String result;

    private TaskStatusPO[] subTaskStatus;

    public TaskStatusPO() {

    }

    public static TaskStatusPO of(TaskStatus startTest) {
        TaskStatusPO build = new TaskStatusPO();
        return build;
    }

    public TaskStatusPO(String id, String desc, String startTime, String endTime, String result, TaskStatusPO[] subTaskStatus) {
        this.id = id;
        this.desc = desc;
        this.startTime = startTime;
        this.endTime = endTime;
        this.result = result;
        this.subTaskStatus = subTaskStatus;
    }

    @Override
    public TaskStatus toDomainModel() {
        return new TaskStatus();
    }
}
