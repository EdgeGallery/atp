package org.edgegallery.atp.domain.model.test;

import lombok.Getter;
import lombok.Setter;
import org.edgegallery.atp.domain.model.user.User;
import org.edgegallery.atp.domain.shared.Entity;

@Getter
@Setter
public class TaskStatus implements Entity {

    private String id;

    private String fileName;

    private String testCaseName;

    private String testCaseDesc;

    private String startTime;

    private String endTime;

    private String status;

    private User user;

    private String log;

    private TaskStatus[] subTaskStatus;

    public TaskStatus() {
    }


    public TaskStatus(Builder builder) {
        this.id = builder.id;
        this.fileName = builder.fileName;
        this.testCaseName = builder.testCaseName;
        this.testCaseDesc = builder.testCaseDesc;
        this.startTime = builder.startTime;
        this.endTime = builder.endTime;
        this.status = builder.status;
        this.user = builder.user;
        this.log = builder.log;
        this.subTaskStatus = builder.subTaskStatus;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String id;

        private String fileName;

        private String testCaseName;

        private String testCaseDesc;

        private String startTime;

        private String endTime;

        private String status;

        private User user;

        private String log;

        private TaskStatus[] subTaskStatus;

        private Builder() {
            // private construct
        }

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder setTestCaseName(String testCaseName) {
            this.testCaseName = testCaseName;
            return this;
        }

        public Builder setTestCaseDesc(String testCaseDesc) {
            this.testCaseDesc = testCaseDesc;
            return this;
        }

        public Builder setStartTime(String startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder setEndTime(String endTime) {
            this.endTime = endTime;
            return this;
        }

        public Builder setStatus(String status) {
            this.status = status;
            return this;
        }

        public Builder setUser(User user) {
            this.user = user;
            return this;
        }

        public Builder setLog(String log) {
            this.log = log;
            return this;
        }

        public Builder setSubTaskStatus(TaskStatus[] subTaskStatus) {
            this.subTaskStatus = subTaskStatus;
            return this;
        }

        public TaskStatus build() {
            return new TaskStatus(this);
        }
    }
}
