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

package org.edgegallery.atp.repository.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.edgegallery.atp.constant.ErrorCode;
import org.edgegallery.atp.model.config.Config;
import org.edgegallery.atp.repository.mapper.ConfigMapper;
import org.edgegallery.atp.utils.exception.IllegalRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ConfigRepositoryImpl implements ConfigRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigRepositoryImpl.class);

    @Autowired
    ConfigMapper configMapper;

    @Override
    public void insert(Config config) {
        try {
            configMapper.insert(config);
        } catch (Exception e) {
            LOGGER.error("insert config failed. {}", e);
            throw new IllegalRequestException(String.format(ErrorCode.DB_ERROR_MSG, "insert config failed"),
                ErrorCode.DB_ERROR, new ArrayList<String>(Arrays.asList("insert config failed")));
        }
    }

    @Override
    public Config queryConfigById(String id) {
        try {
            return configMapper.queryConfigById(id);
        } catch (Exception e) {
            LOGGER.error("query config by id failed. {}", e);
            throw new IllegalRequestException(String.format(ErrorCode.DB_ERROR_MSG, "query config by id failed"),
                ErrorCode.DB_ERROR, new ArrayList<String>(Arrays.asList("query config by id failed")));
        }
    }

    @Override
    public void updateConfig(Config config) {
        try {
            configMapper.updateConfig(config);
        } catch (Exception e) {
            LOGGER.error("update a config failed. {}", e);
            throw new IllegalRequestException(String.format(ErrorCode.DB_ERROR_MSG, "update a config failed"),
                ErrorCode.DB_ERROR, new ArrayList<String>(Arrays.asList("update a config failed")));
        }
    }

    @Override
    public void deleteConfig(String id) {
        try {
            configMapper.deleteConfig(id);
        } catch (Exception e) {
            LOGGER.error("delete a config failed. {}", e);
            throw new IllegalRequestException(String.format(ErrorCode.DB_ERROR_MSG, "delete a config failed"),
                ErrorCode.DB_ERROR, new ArrayList<String>(Arrays.asList("delete a config failed")));
        }
    }

    @Override
    public int countTotal(String nameCh, String nameEn) {
        try {
            return configMapper.countTotal(nameCh, nameEn);
        } catch (Exception e) {
            LOGGER.error("get config total count failed. {}", e);
            throw new IllegalRequestException(String.format(ErrorCode.DB_ERROR_MSG, "get config total count failed"),
                ErrorCode.DB_ERROR, new ArrayList<String>(Arrays.asList("get config total count failed")));
        }
    }

    @Override
    public List<Config> getAllWithPagination(int limit, int offset, String nameCh, String nameEn) {
        try {
            return configMapper.getAllWithPagination(limit, offset, nameCh, nameEn);
        } catch (Exception e) {
            LOGGER.error("get all configs with pagination failed. {}", e);
            throw new IllegalRequestException(
                String.format(ErrorCode.DB_ERROR_MSG, "get all configs with pagination failed"), ErrorCode.DB_ERROR,
                new ArrayList<String>(Arrays.asList("get all configs with pagination failed")));
        }
    }
}
