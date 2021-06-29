/*
 * Copyright 2021 Huawei Technologies Co., Ltd.
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

package org.edgegallery.atp.model.contribution;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(value = {"filePath"})
public class Contribution {
    /**
     * id.
     */
    private String id;

    /**
     * test case name.
     */
    private String name;

    /**
     * test objective.
     */
    private String objective;

    /**
     * test step.
     */
    private String step;

    /**
     * test expect result.
     */
    private String expectResult;

    /**
     * type: text or script.
     */
    private String type;

    /**
     * file create time.
     */
    private Date createTime;

    /**
     * file path.
     */
    private String filePath;

    /**
     * get create time.
     * 
     * @return date
     */
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


    public Contribution() {

    }

    /**
     * construct function.
     * 
     * @param builder builder
     */
    public Contribution(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.objective = builder.objective;
        this.step = builder.step;
        this.expectResult = builder.expectResult;
        this.type = builder.type;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String id;

        private String name;

        private String objective;

        private String step;

        private String expectResult;

        private String type;

        private Builder() {

        }

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setObjective(String objective) {
            this.objective = objective;
            return this;
        }

        public Builder setStep(String step) {
            this.step = step;
            return this;
        }

        public Builder setExpectResult(String expectResult) {
            this.expectResult = expectResult;
            return this;
        }

        public Builder setType(String type) {
            this.type = type;
            return this;
        }


        public Contribution build() {
            return new Contribution(this);
        }
    }
}
