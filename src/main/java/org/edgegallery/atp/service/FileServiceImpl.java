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

package org.edgegallery.atp.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.model.file.AtpFile;
import org.edgegallery.atp.repository.file.FileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service("FileService")
public class FileServiceImpl implements FileService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileServiceImpl.class);

    @Autowired
    FileRepository fileRepository;

    @Override
    public ResponseEntity<byte[]> getFileContent(String fileId, String type) throws FileNotFoundException {
        type = StringUtils.isEmpty(type) ? Constant.FILE_TYPE_SCENARIO : type;
        AtpFile fileInfo = fileRepository.getFileContent(fileId, type);
        if (null == fileInfo) {
            LOGGER.error("fileId does not exists: {}", fileId);
            throw new FileNotFoundException("fileId does not exists");
        }

        File file = new File(fileInfo.getFilePath());
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/octet-stream");
            headers.add("Content-Disposition", "attachment; filename=" + fileInfo.getFileId());
            byte[] fileData = FileUtils.readFileToByteArray(file);
            LOGGER.info("get file content successfully.");
            return ResponseEntity.ok().headers(headers).body(fileData);
        } catch (IOException e) {
            String msg = "download file failed.";
            LOGGER.error(msg);
            throw new IllegalArgumentException(msg);
        }
    }

}
