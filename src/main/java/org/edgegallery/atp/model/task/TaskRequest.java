package org.edgegallery.atp.model.task;

import org.edgegallery.atp.model.Entity;
import org.edgegallery.atp.model.testcase.TestCaseDetail;
import org.edgegallery.atp.model.user.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskRequest implements Entity {

    private String id;

    private String appName;

    private String appVersion;

    private String status;

    private String createTime;

    private String endTime;

    private User user;

    private TestCaseDetail testCaseDetail;

    public TaskRequest() {}

    public TaskRequest(Builder builder) {
        this.id = builder.id;
        this.appName = builder.appName;
        this.appVersion = builder.appVersion;
        this.status = builder.status;
        this.createTime = builder.createTime;
        this.endTime = builder.endTime;
        this.user = builder.user;
        this.testCaseDetail = builder.testCaseDetail;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String id;

        private String appName;

        private String appVersion;

        private String status;

        private String createTime;

        private String endTime;

        private User user;

        private TestCaseDetail testCaseDetail;

        private Builder() {
            // private construct
        }

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setAppName(String appName) {
            this.appName = appName;
            return this;
        }

        public Builder setAppVersion(String appVersion) {
            this.appVersion = appVersion;
            return this;
        }

        public Builder setStatus(String status) {
            this.status = status;
            return this;
        }

        public Builder setCreateTime(String createTime) {
            this.createTime = createTime;
            return this;
        }

        public Builder setEndTime(String endTime) {
            this.endTime = endTime;
            return this;
        }

        public Builder setUser(User user) {
            this.user = user;
            return this;
        }

        public Builder setTestCaseDetail(TestCaseDetail testCaseDetail) {
            this.testCaseDetail = testCaseDetail;
            return this;
        }

        public TaskRequest build() {
            return new TaskRequest(this);
        }
    }
}
