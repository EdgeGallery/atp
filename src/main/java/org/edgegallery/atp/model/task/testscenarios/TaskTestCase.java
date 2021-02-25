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
package org.edgegallery.atp.model.task.testscenarios;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskTestCase {

    /**
     * test case id
     */
    String id;

    /**
     * test case chinese name
     */
    String nameCh;

    /**
     * test case english name
     */
    String nameEn;

    /**
     * test case chinese description
     */
    String descriptionCh;

    /**
     * test case english description
     */
    String descriptionEn;

    /**
     * test case type: automatic or manual
     */
    String type;

    /**
     * test case execute result. The value is enum:success,failed or running.
     */
    String result;

    /**
     * test case fail reason,it can be empty when the result is not failed.
     */
    String reason;
}
