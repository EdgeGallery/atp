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

package org.edgegallery.atp.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import org.apache.commons.lang3.StringUtils;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.model.file.ATPFile;
import org.edgegallery.atp.repository.file.FileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service("FileService")
public class FileServiceImpl implements FileService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileServiceImpl.class);

    @Autowired
    FileRepository fileRepository;

    @Override
    public ResponseEntity<InputStreamResource> getFileContent(String fileId, String type) throws FileNotFoundException {
        type = StringUtils.isEmpty(type) ? Constant.FILE_TYPE_SCENARIO : type;
        ATPFile fileInfo = fileRepository.getFileContent(fileId, type);
        if (null == fileInfo) {
            LOGGER.error("fileId does not exists: {}", fileId);
            throw new FileNotFoundException("fileId does not exists");
        }

        File file = new File(fileInfo.getFilePath());
        try {
            InputStream fileContent = new FileInputStream(file);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/octet-stream");
            LOGGER.info("get file content successfully.");
            return new ResponseEntity<>(new InputStreamResource(fileContent), headers, HttpStatus.OK);
        } catch (FileNotFoundException e) {
            String msg = "file not exists.";
            LOGGER.error(msg);
            throw new IllegalArgumentException(msg);
        }
    }

}
