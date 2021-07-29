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

package org.edgegallery.atp.repository.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.edgegallery.atp.model.config.Config;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface ConfigMapper {
    /**
     * insert config into db.
     *
     * @param config config info
     */
    void insert(Config config);

    /**
     * query config by id.
     *
     * @param id config id
     * @return config info
     */
    Config queryConfigById(String id);

    /**
     * update config.
     *
     * @param config config info
     */
    void updateConfig(Config config);

    /**
     * delete config.
     *
     * @param id config id
     */
    void deleteConfig(String id);

    /**
     * get total count of configs.
     *
     * @param nameCh nameCh
     * @param nameEn nameEn
     * @return total nums
     */
    int countTotal(@Param("nameCh") String nameCh, @Param("nameEn") String nameEn);

    /**
     * get all configs with pagination.
     *
     * @param limit limit
     * @param offset offset
     * @param nameCh nameCh
     * @param nameEn nameEn
     * @return config info list
     */
    List<Config> getAllWithPagination(@Param("limit") int limit, @Param("offset") int offset,
        @Param("nameCh") String nameCh, @Param("nameEn") String nameEn);
}