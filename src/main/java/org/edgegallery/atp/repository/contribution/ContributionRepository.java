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
import java.util.Map;
import org.edgegallery.atp.model.contribution.Contribution;

public interface ContributionRepository {

    /**
     * insert contribution.
     * 
     * @param contribution contribution
     */
    void insert(Contribution contribution);

    /**
     * get all contributions.
     *
     * @param name contribution name
     * @return contribution list
     */
    List<Contribution> getAllContributions(String name);

    /**
     * get contribution count.
     *
     * @param name name
     * @return contribution count
     */
    int countTotal(String name);

    /**
     * get all contributions with pagination.
     *
     * @param limit limit
     * @param offset offset
     * @param name name
     * @return contributions info list
     */
    List<Contribution> getAllWithPagination(int limit, int offset, String name);

    /**
     * batch delete contributions by contribution ids.
     *
     * @param ids contribution ids
     * @return failed id list
     */
    Map<String, List<String>> batchDelete(List<String> ids);

    /**
     * get contribution by contribution id.
     * 
     * @param id contribution id
     * @return contribution info
     */
    Contribution getContributionById(String id);

    /**
     * get contribution by contribution name.
     * 
     * @param name name
     * @return contribution info
     */
    Contribution getContributionByName(String name);
}
