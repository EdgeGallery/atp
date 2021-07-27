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

package org.edgegallery.atp.model.config;

import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.edgegallery.atp.constant.Constant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConfigBase {
    /**
     * chinese name.
     */
    @Size(max = Constant.LENGTH_64, message = "config nameCh must not exceed more than 64 characters") String nameCh;

    /**
     * english name.
     */
    @Size(max = Constant.LENGTH_64, message = "config nameEn must not exceed more than 64 characters") String nameEn;

    /**
     * config chinese description.
     */
    @Size(max = Constant.LENGTH_255, message = "config descriptionCh must not exceed more than 255 characters") String
        descriptionCh;

    /**
     * config english description.
     */
    @Size(max = Constant.LENGTH_255, message = "config descriptionEn must not exceed more than 255 characters") String
        descriptionEn;

    /**
     * config params.
     */
    @Size(max = Constant.LENGTH_255, min = 1,
        message = "configuration params must not exceed more than 255 characters") String configuration;
}
