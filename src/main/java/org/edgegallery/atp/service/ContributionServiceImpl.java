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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.constant.ErrorCode;
import org.edgegallery.atp.model.PageResult;
import org.edgegallery.atp.model.contribution.Contribution;
import org.edgegallery.atp.repository.contribution.ContributionRepository;
import org.edgegallery.atp.repository.task.TaskRepository;
import org.edgegallery.atp.utils.CommonUtil;
import org.edgegallery.atp.utils.FileChecker;
import org.edgegallery.atp.utils.exception.IllegalRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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
            LOGGER.error("contribution name alreay exists.");
            throw new IllegalRequestException(String.format(ErrorCode.NAME_EXISTS_MSG, contribution.getName()),
                ErrorCode.NAME_EXISTS, new ArrayList<String>(Arrays.asList(contribution.getName())));
        }

        contribution.setId(CommonUtil.generateId());
        contribution.setCreateTime(taskRepository.getCurrentDate());
        if (Constant.CONTRIBUTION_TYPE_SCRIPT.equals(contribution.getType()) && null != file && 0 != (int) file
            .getSize()) {
            String fileName = file.getOriginalFilename();
            if (null != fileName && !fileName.endsWith(Constant.ZIP)) {
                LOGGER.error("file pattern is wrong, must zip pattern.");
                throw new IllegalRequestException(String.format(ErrorCode.PATTERN_CHECK_FAILED_MSG, Constant.ZIP),
                    ErrorCode.PATTERN_CHECK_FAILED, new ArrayList<String>(Arrays.asList(Constant.ZIP)));
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
                throw new IllegalRequestException(ErrorCode.FILE_IO_EXCEPTION_MSG, ErrorCode.FILE_IO_EXCEPTION, null);
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
    public PageResult<Contribution> getAllByPagination(String name, int limit, int offset) {
        try {
            PageResult<Contribution> pageResult = new PageResult<Contribution>(offset, limit);
            pageResult.setTotal(contributionRepository.countTotal(name));
            pageResult.setResults(contributionRepository.getAllWithPagination(limit, offset, name));
            LOGGER.info("query all contributions by pagination successfully.");
            return pageResult;
        } catch (Exception e) {
            LOGGER.error("query all contributions by pagination failed. {}", e);
            return null;
        }
    }

    @Override
    public Map<String, List<String>> batchDelete(List<String> ids) {
        Map<String, List<String>> failedIdList = contributionRepository.batchDelete(ids);
        LOGGER.info("batch delete contributions by ids successfully.");
        return failedIdList;
    }

    @Override
    public ResponseEntity<byte[]> downloadContributions(String id) {
        Contribution contribution = contributionRepository.getContributionById(id);
        if (null == contribution) {
            String msg = "contribution not exists.";
            LOGGER.error(msg);
            throw new IllegalArgumentException(msg);
        }
        File file = new File(contribution.getFilePath());
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/octet-stream");
            headers.add("Content-Disposition", "attachment; filename=" + contribution.getName());
            byte[] fileData = FileUtils.readFileToByteArray(file);
            LOGGER.info("download contribution successfully.");
            return ResponseEntity.ok().headers(headers).body(fileData);
        } catch (IOException e) {
            String msg = "download contribution test case failed.";
            LOGGER.error(msg);
            throw new IllegalArgumentException(msg);
        }
    }
}
