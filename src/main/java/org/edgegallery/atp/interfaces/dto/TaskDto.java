package org.edgegallery.atp.interfaces.dto;

import org.edgegallery.atp.model.task.TaskStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskDto {

    private String id;

    private String desc;

    private String startTime;

    private String endTime;

    private String result;

    private TaskDto[] subTaskStatus;

    public TaskDto() {
    }

    public static TaskDto of(TaskStatus status) {
        TaskDto build = new TaskDto();
        build.setId(status.getId());
        build.setStartTime(status.getStartTime());
        build.setEndTime(status.getEndTime());
        return build;
    }
}
