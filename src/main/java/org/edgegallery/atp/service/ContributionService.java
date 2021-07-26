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

import java.util.List;
import java.util.Map;
import org.edgegallery.atp.model.contribution.Contribution;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface ContributionService {
    /**
     * create contribution to db.
     * 
     * @param contribution contribution info
     * @param file script file
     * @return contribution file
     */
    Contribution createContribution(Contribution contribution, MultipartFile file);

    /**
     * get all contributions.
     * 
     * @param name contribution name
     * @returnContribution list
     */
    List<Contribution> getAllContribution(String name);

    /**
     * batch delete contributions by id list.
     * 
     * @param ids contribution id list
     * @return failed id list
     */
    Map<String, List<String>> batchDelete(List<String> ids);

    /**
     * download script contributions.
     *
     * @param id contribution id
     * @return file stream
     */
    ResponseEntity<byte[]> downloadContributions(String id);
}
