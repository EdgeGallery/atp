/*
 * Copyright 2020 Huawei Technologies Co., Ltd.
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

package org.edgegallery.atp.model.testscenario;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestScenario {

    /**
     * test scenario id.
     */
    private String id;

    /**
     * test scenario chinese name.
     */
    private String nameCh;

    /**
     * test scenario english name.
     */
    private String nameEn;

    /**
     * test scenario chinese description.
     */
    private String descriptionCh;

    /**
     * test scenario english description.
     */
    private String descriptionEn;

    /**
     * test scenario label,unum value:EdgeGallery,China Mobile,China Unicom,China Telecom.
     */
    private String label;

    public TestScenario() {

    }

    public TestScenario(Builder builder) {
        this.id = builder.id;
        this.nameEn = builder.nameEn;
        this.nameCh = builder.nameCh;
        this.descriptionEn = builder.descriptionEn;
        this.descriptionCh = builder.descriptionCh;
        this.label = builder.label;
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

        private String label;

        private Builder() {

        }

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setnameCh(String nameCh) {
            this.nameCh = nameCh;
            return this;
        }

        public Builder setNameEn(String nameEn) {
            this.nameEn = nameEn;
            return this;
        }

        public Builder setdescriptionCh(String descriptionCh) {
            this.descriptionCh = descriptionCh;
            return this;
        }

        public Builder setDescriptionEn(String descriptionEn) {
            this.descriptionEn = descriptionEn;
            return this;
        }

        public Builder setLabel(String label) {
            this.label = label;
            return this;
        }

        public TestScenario build() {
            return new TestScenario(this);
        }
    }
}
