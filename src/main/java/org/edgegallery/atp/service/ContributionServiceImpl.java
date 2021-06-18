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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.model.contribution.Contribution;
import org.edgegallery.atp.repository.contribution.ContributionRepository;
import org.edgegallery.atp.repository.task.TaskRepository;
import org.edgegallery.atp.utils.CommonUtil;
import org.edgegallery.atp.utils.FileChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service("ContributionService")
public class ContributionServiceImpl implements ContributionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContributionServiceImpl.class);

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    ContributionRepository contributionRepository;


    @Override
    public Contribution createContribution(Contribution contribution, MultipartFile file) {
        if (null != contributionRepository.getContributionByName(contribution.getName())) {
            String msg = "contribution name alreay exists.";
            LOGGER.error(msg);
            throw new IllegalArgumentException(msg);
        }

        contribution.setId(CommonUtil.generateId());
        contribution.setCreateTime(taskRepository.getCurrentDate());
        if (Constant.CONTRIBUTION_TYPE_SCRIPT.equals(contribution.getType()) && null != file
                && 0 != (int) file.getSize()) {
            String fileName = file.getOriginalFilename();
            if (null != fileName && !fileName.endsWith(Constant.ZIP)) {
                String msg = "file pattern is wrong, must zip pattern.";
                LOGGER.error(msg);
                throw new IllegalArgumentException(msg);
            }
            // save script file
            String filePath = Constant.BASIC_CONTRIBUTION_PATH.concat(contribution.getId());
            try {
                FileChecker.createFile(filePath);
                File result = new File(filePath);
                file.transferTo(result);
                contribution.setFilePath(filePath);
            } catch (IOException e) {
                LOGGER.error("create file failed, contribution name is: {}", contribution.getName());
                throw new IllegalArgumentException("create file failed.");
            }
        }

        contributionRepository.insert(contribution);
        LOGGER.info("create contribution successfully.");
        return contribution;
    }


    @Override
    public List<Contribution> getAllContribution(String name) {
        List<Contribution> contributionList = contributionRepository.getAllContributions(name);
        LOGGER.info("query all contributions successfully.");
        return contributionList;
    }

    @Override
    public Map<String, List<String>> batchDelete(List<String> ids) {
        Map<String, List<String>> failedIdList = contributionRepository.batchDelete(ids);
        LOGGER.info("batch delete contributions by ids successfully.");
        return failedIdList;
    }


    @Override
    public ResponseEntity<InputStreamResource> downloadContributions(String id) {
        Contribution contribution = contributionRepository.getContributionById(id);
        if (null == contribution) {
            String msg = "contribution not exists.";
            LOGGER.error(msg);
            throw new IllegalArgumentException(msg);
        }
        File file = new File(contribution.getFilePath());
        try {
            InputStream fileContent = new FileInputStream(file);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/octet-stream");
            LOGGER.info("download contribution successfully.");
            return new ResponseEntity<>(new InputStreamResource(fileContent), headers, HttpStatus.OK);
        } catch (FileNotFoundException e) {
            String msg = "file not exists.";
            LOGGER.error(msg);
            throw new IllegalArgumentException(msg);
        }
    }
}
