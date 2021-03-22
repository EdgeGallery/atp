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

package org.edgegallery.atp.model.file;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ATPFile {
    /**
     * file id.
     */
    private String fileId;
    
    /**
     * file type:scenario.
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
    
    public Date getCreateTime() {
        return createTime != null?(Date) createTime.clone():null;
    }

    public void setCreateTime(Date createTime) {
        if (createTime != null) {
            this.createTime = (Date) createTime.clone();
        } else {
            this.createTime = null;
        }
    }
    
    public ATPFile(String fileId,String type,Date createTime,String filePath) {
        this.filePath = filePath;
        this.type = type;
        this.fileId = fileId;
        if (createTime != null) {
            this.createTime = (Date) createTime.clone();
        } else {
            this.createTime = null;
        }
    }
}
