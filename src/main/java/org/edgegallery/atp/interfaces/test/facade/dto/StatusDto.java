package org.edgegallery.atp.interfaces.test.facade.dto;

import lombok.Getter;
import lombok.Setter;
import org.edgegallery.atp.domain.model.test.TaskStatus;

@Getter
@Setter
public class StatusDto {

    private String id;

    private String desc;

    private String startTime;

    private String endTime;

    private String result;

    private String userId;

    private String userName;

    private StatusDto[] subTaskStatus;

    public StatusDto() {
    }

    public static StatusDto of(TaskStatus status) {
        StatusDto build = new StatusDto();
        build.setId(status.getId());
        build.setDesc(status.getDesc());
        build.setDesc(status.getDesc());
        build.setEndTime(status.getEndTime());
        return build;
    }
}
