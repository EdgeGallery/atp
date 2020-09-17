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

package org.edgegallery.atp.model.page;

public class PageCriteria {

    private int limit;

    private long offset;

    private String id;

    /**
     * Constructor of PageCriteria.
     */
    public PageCriteria(int limit, long offset, String id) {
        if (limit < 1) {
            throw new IllegalArgumentException("limit must >= 1");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("offset must >= 0");
        }
        this.limit = limit;
        this.offset = offset;
        this.id = id;
    }

    public int getLimit() {
        return limit;
    }

    public long getOffset() {
        return offset;
    }

    public String getId() {
        return id;
    }
}
