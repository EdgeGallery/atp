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

package org.edgegallery.atp.model.testsuite;

import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestSuite {

    /**
     * test suite id.
     */
    private String id;

    /**
     * test suite chinese name.
     */
    private String nameCh;

    /**
     * test suite english name.
     */
    private String nameEn;

    /**
     * test suite chinese description.
     */
    private String descriptionCh;

    /**
     * test suite english description.
     */
    private String descriptionEn;

    /**
     * the test scenario id list that the test suite belong to.
     */
    private List<String> scenarioIdList;

    /**
     * create time.
     */
    private Date createTime;

    public TestSuite() {

    }

    /**
     * construct function.
     * 
     * @param builder builder
     */
    public TestSuite(Builder builder) {
        this.id = builder.id;
        this.nameCh = builder.nameCh;
        this.nameEn = builder.nameEn;
        this.descriptionCh = builder.descriptionCh;
        this.descriptionEn = builder.descriptionEn;
        this.createTime = builder.createTime;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String id;

        private String nameCh;

        private String nameEn;

        private String descriptionCh;

        private String descriptionEn;

        private Date createTime;

        private Builder() {

        }

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setNameEn(String nameEn) {
            this.nameEn = nameEn;
            return this;
        }

        public Builder setNameCh(String nameCh) {
            this.nameCh = nameCh;
            return this;
        }

        public Builder setDescriptionEn(String descriptionEn) {
            this.descriptionEn = descriptionEn;
            return this;
        }

        public Builder setDescriptionCh(String descriptionCh) {
            this.descriptionCh = descriptionCh;
            return this;
        }

        public Builder setCreateTime(Date createTime) {
            this.createTime = createTime;
            return this;
        }

        public TestSuite build() {
            return new TestSuite(this);

        }
    }
}
