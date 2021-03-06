/*
 * Copyright 2020-2021 Huawei Technologies Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.edgegallery.atp.model.task;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.edgegallery.atp.model.Entity;
import org.edgegallery.atp.model.task.testscenarios.TaskTestScenario;
import org.edgegallery.atp.model.user.User;

@Getter
@Setter
@JsonIgnoreProperties(value = {"packagePath"})
public class TaskRequest implements Entity {

    private String id;

    private String appName;

    private String appVersion;

    private String status;

    private Date createTime;

    private Date endTime;

    private User user;

    private List<TaskTestScenario> testScenarios;

    private String accessToken;

    private String providerId;

    private String packagePath;

    public Date getCreateTime() {
        return createTime != null ? (Date) createTime.clone() : null;
    }

    /**
     * set create time.
     * 
     * @param createTime createTime
     */
    public void setCreateTime(Date createTime) {
        if (createTime != null) {
            this.createTime = (Date) createTime.clone();
        } else {
            this.createTime = null;
        }
    }

    public Date getEndTime() {
        return endTime != null ? (Date) endTime.clone() : null;
    }

    /**
     * set end time.
     * 
     * @param endTime endTime
     */
    public void setEndTime(Date endTime) {
        if (endTime != null) {
            this.endTime = (Date) endTime.clone();
        } else {
            this.endTime = null;
        }
    }

    public TaskRequest() {}

    /**
     * construct function.
     * 
     * @param builder builder
     */
    public TaskRequest(Builder builder) {
        this.id = builder.id;
        this.appName = builder.appName;
        this.appVersion = builder.appVersion;
        this.status = builder.status;
        this.user = builder.user;
        this.testScenarios = builder.testScenarios;
        this.packagePath = builder.packagePath;
        this.providerId = builder.providerId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String id;

        private String appName;

        private String appVersion;

        private String status;

        private String providerId;

        private String packagePath;

        private User user;

        private List<TaskTestScenario> testScenarios;

        private Builder() {
            // private construct
        }

        public Builder setProviderId(String providerId) {
            this.providerId = providerId;
            return this;
        }

        public Builder setPackagePath(String packagePath) {
            this.packagePath = packagePath;
            return this;
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

        public Builder setUser(User user) {
            this.user = user;
            return this;
        }

        public Builder setTestCaseDetail(List<TaskTestScenario> testScenarios) {
            this.testScenarios = testScenarios;
            return this;
        }

        public TaskRequest build() {
            return new TaskRequest(this);
        }
    }
}
