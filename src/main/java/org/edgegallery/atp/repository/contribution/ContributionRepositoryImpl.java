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

import java.util.List;
import org.edgegallery.atp.model.contribution.Contribution;
import org.edgegallery.atp.repository.mapper.ContributionMapper;
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
            throw new IllegalArgumentException("insert contribution failed.");
        }
    }

    @Override
    public List<Contribution> getAllContributions() {
        try {
            return contributionMapper.getAllContributions();
        } catch (Exception e) {
            LOGGER.error("query all contributions failed. {}", e);
            throw new IllegalArgumentException("query all contributions failed.");
        }
    }
}
