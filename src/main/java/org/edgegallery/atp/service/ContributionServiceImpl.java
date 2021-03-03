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
import java.io.IOException;
import java.util.List;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.model.contribution.Contribution;
import org.edgegallery.atp.repository.contribution.ContributionRepository;
import org.edgegallery.atp.repository.task.TaskRepository;
import org.edgegallery.atp.utils.CommonUtil;
import org.edgegallery.atp.utils.FileChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
        contribution.setId(CommonUtil.generateId());
        contribution.setCreateTime(taskRepository.getCurrentDate());
        if (Constant.CONTRIBUTION_TYPE_SCRIPT.equals(contribution.getType()) && null != file
                && 0 != (int) file.getSize()) {
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
    public List<Contribution> getAllContribution() {
        List<Contribution> contributionList = contributionRepository.getAllContributions();
        LOGGER.info("query all contributions successfully.");
        return contributionList;
    }
}
