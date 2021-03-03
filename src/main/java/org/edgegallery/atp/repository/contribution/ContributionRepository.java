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
package org.edgegallery.atp.repository.contribution;

import java.util.List;
import org.edgegallery.atp.model.contribution.Contribution;

public interface ContributionRepository {

    /**
     * insert contribution
     * 
     * @param contribution
     */
    public void insert(Contribution contribution);

    /**
     * get all contributions
     * 
     * @return
     */
    public List<Contribution> getAllContributions();
}
