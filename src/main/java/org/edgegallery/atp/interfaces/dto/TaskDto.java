package org.edgegallery.atp.interfaces.dto;

import java.util.Date;
import org.edgegallery.atp.model.task.TaskRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskDto {

    private String id;

    private String desc;

    private Date startTime;

    private Date endTime;

    private String result;

    private TaskDto[] subTaskStatus;

    public TaskDto() {}

    public static TaskDto of(TaskRequest task) {
        TaskDto build = new TaskDto();
        build.setId(task.getId());
        build.setStartTime(task.getCreateTime());
        build.setEndTime(task.getEndTime());
        return build;
    }
}
