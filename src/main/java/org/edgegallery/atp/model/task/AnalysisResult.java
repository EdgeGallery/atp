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

package org.edgegallery.atp.model.task;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AnalysisResult {
    /**
     * total number of test task.
     */
    int total = 0;

    /**
     * current month test task number.
     */
    int currentMonth = 0;

    /**
     * one month ago test task number.
     */
    int oneMonthAgo = 0;

    /**
     * two month ago test task number.
     */
    int twoMonthAgo = 0;

    /**
     * three month ago test task number.
     */
    int threeMonthAgo = 0;

    /**
     * four month ago test task number.
     */
    int fourMonthAgo = 0;

    /**
     * five month ago test task number.
     */
    int fiveMonthAgo = 0;

    public void increaseCurrentMonth() {
        this.currentMonth++;
    }

    public void increaseOneMonthAgo() {
        this.oneMonthAgo++;
    }

    public void increaseTwoMonthAgo() {
        this.twoMonthAgo++;
    }

    public void increaseThreeMonthAgo() {
        this.threeMonthAgo++;
    }

    public void increaseFourMonthAgo() {
        this.fourMonthAgo++;
    }

    public void increaseFiveMonthAgo() {
        this.fiveMonthAgo++;
    }

    public void setTotal() {
        this.total = this.currentMonth + this.oneMonthAgo + this.twoMonthAgo + this.threeMonthAgo + this.fourMonthAgo
                + this.fiveMonthAgo;
    }

}
