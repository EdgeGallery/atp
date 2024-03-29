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

package org.edgegallery.atp.repository.contribution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.constant.ErrorCode;
import org.edgegallery.atp.model.contribution.Contribution;
import org.edgegallery.atp.repository.mapper.ContributionMapper;
import org.edgegallery.atp.utils.CommonUtil;
import org.edgegallery.atp.utils.exception.IllegalRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ContributionRepositoryImpl implements ContributionRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContributionRepositoryImpl.class);

    @Autowired
    ContributionMapper contributionMapper;

    @Override
    public void insert(Contribution contribution) {
        try {
            contributionMapper.insert(contribution);
        } catch (Exception e) {
            LOGGER.error("insert contribution failed. {}", e);
            throw new IllegalRequestException(String.format(ErrorCode.DB_ERROR_MSG, "insert contribution failed"),
                    ErrorCode.DB_ERROR, new ArrayList<String>(Arrays.asList("insert contribution failed")));
        }
    }

    @Override
    public List<Contribution> getAllContributions(String name) {
        try {
            return contributionMapper.getAllContributions(name);
        } catch (Exception e) {
            LOGGER.error("query all contributions failed. {}", e);
            throw new IllegalRequestException(String.format(ErrorCode.DB_ERROR_MSG, "query all contributions failed"),
                ErrorCode.DB_ERROR, new ArrayList<String>(Arrays.asList("query all contributions failed")));
        }
    }

    @Override
    public int countTotal(String name) {
        try {
            return contributionMapper.countTotal(name);
        } catch (Exception e) {
            LOGGER.error("get contribution total count failed. {}", e);
            throw new IllegalRequestException(
                String.format(ErrorCode.DB_ERROR_MSG, "get contribution total count failed"), ErrorCode.DB_ERROR,
                new ArrayList<String>(Arrays.asList("get contribution total count failed")));
        }
    }

    @Override
    public List<Contribution> getAllWithPagination(int limit, int offset, String name) {
        try {
            return contributionMapper.getAllWithPagination(limit, offset, name);
        } catch (Exception e) {
            LOGGER.error("get all contribution with pagination failed. {}", e);
            throw new IllegalRequestException(
                String.format(ErrorCode.DB_ERROR_MSG, "get all contribution with pagination failed"),
                ErrorCode.DB_ERROR,
                new ArrayList<String>(Arrays.asList("get all contribution with pagination failed")));
        }
    }

    @Override
    public Map<String, List<String>> batchDelete(List<String> ids) {
        Map<String, List<String>> result = new HashMap<String, List<String>>();
        List<String> failIds = new ArrayList<String>();
        for (String id : ids) {
            try {
                Contribution contribution = contributionMapper.getContributionById(id);
                contributionMapper.deleteContributionsById(id);
                if (null != contribution.getFilePath()) {
                    CommonUtil.deleteFile(contribution.getFilePath());
                }
            } catch (Exception e) {
                LOGGER.error("delete contribution by id {} failed. {}", id, e);
                failIds.add(id);
            }
        }

        result.put(Constant.FAILED, failIds);
        return result;
    }

    @Override
    public Contribution getContributionById(String id) {
        try {
            return contributionMapper.getContributionById(id);
        } catch (Exception e) {
            LOGGER.error("query contribution by id failed. {}", e);
            throw new IllegalRequestException(String.format(ErrorCode.DB_ERROR_MSG, "query contribution by id failed"),
                    ErrorCode.DB_ERROR, new ArrayList<String>(Arrays.asList("query contribution by id failed")));
        }
    }

    @Override
    public Contribution getContributionByName(String name) {
        try {
            return contributionMapper.getContributionByName(name);
        } catch (Exception e) {
            LOGGER.error("query contribution by name failed. {}", e);
            throw new IllegalRequestException(
                    String.format(ErrorCode.DB_ERROR_MSG, "query contribution by name failed"), ErrorCode.DB_ERROR,
                    new ArrayList<String>(Arrays.asList("query contribution by name failed")));
        }
    }
}
