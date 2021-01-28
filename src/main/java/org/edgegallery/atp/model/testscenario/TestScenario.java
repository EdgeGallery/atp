/*
 *    Copyright 2020 Huawei Technologies Co., Ltd.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.edgegallery.atp.model.testscenario;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestScenario {

    /**
     * test scenario id
     */
    String id;
    
    /**
     * test scenario chinese name
     */
    @JsonProperty("name_zh")
    String nameZh;
    
    /**
     * test scenario english name
     */
    @JsonProperty("name_en")
    String nameEn;
    
    /**
     * test scenario chinese description
     */
    @JsonProperty("description_zh")
    String descriptionZh;
    
    /**
     * test scenario english description
     */
    @JsonProperty("description_en")
    String descriptionEn;
    
    public TestScenario(Builder builder) {
        this.id = builder.id;
        this.nameEn = builder.nameEn;
        this.nameZh = builder.nameZh;
        this.descriptionEn = builder.descriptionEn;
        this.descriptionZh = builder.descriptionZh;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static final class Builder{
        String id;
        
        String nameZh;
        
        String nameEn;
        
        String descriptionZh;
        
        String descriptionEn;
        
        private Builder() {
            
        }
        
        public Builder setId(String id) {
            this.id = id;
            return this;
        }
        
        public Builder setNameZh(String nameZh) {
            this.nameZh = nameZh;
            return this;
        }
        
        public Builder setNameEn(String nameEn) {
            this.nameEn = nameEn;
            return this;
        }
        
        public Builder setDescriptionZh(String descriptionZh) {
            this.descriptionZh = descriptionZh;
            return this;
        }
        
        public Builder setDescriptionEn(String descriptionEn) {
            this.descriptionEn = descriptionEn;
            return this;
        }
        
        public TestScenario build() {
            return new TestScenario(this);
        }
    }
}
