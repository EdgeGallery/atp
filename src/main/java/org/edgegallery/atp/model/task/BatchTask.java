package org.edgegallery.atp.model.task;

import javax.persistence.Column;
import javax.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class BatchTask {
    @Column(name = "id")
    private String id;

    @Column(name = "subTaskId")
    private String subTaskId;

    @Column(name = "userId")
    private String userId;

    @Column(name = "userName")
    private String userName;
}
