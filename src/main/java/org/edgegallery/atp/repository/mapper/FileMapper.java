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
package org.edgegallery.atp.repository.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.edgegallery.atp.model.file.ATPFile;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface FileMapper {
    /**
     * get ATPFile info according to fileId
     * 
     * @param fileId file id
     * @param type file type
     * @return ATP file info
     */
    public ATPFile getFileContent(@Param("fileId") String fileId, @Param("type") String type);

    /**
     * insert file info
     * 
     * @param file file info
     */
    public void insertFile(ATPFile file);
}