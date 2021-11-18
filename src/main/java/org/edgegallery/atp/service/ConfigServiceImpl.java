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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.constant.ErrorCode;
import org.edgegallery.atp.model.PageResult;
import org.edgegallery.atp.model.config.Config;
import org.edgegallery.atp.model.config.ConfigBase;
import org.edgegallery.atp.model.testcase.TestCasePo;
import org.edgegallery.atp.repository.config.ConfigRepository;
import org.edgegallery.atp.repository.task.TaskRepository;
import org.edgegallery.atp.repository.testcase.TestCaseRepository;
import org.edgegallery.atp.utils.CommonUtil;
import org.edgegallery.atp.utils.exception.FileNotExistsException;
import org.edgegallery.atp.utils.exception.IllegalRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service("ConfigService")
public class ConfigServiceImpl implements ConfigService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigServiceImpl.class);

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    TestCaseRepository testCaseRepository;

    @Autowired
    ConfigRepository configRepository;

    @Override
    public Config createConfig(ConfigBase configBase) {
        if (StringUtils.isEmpty(configBase.getConfiguration())) {
            LOGGER.error("configuration is null.");
            throw new IllegalRequestException(String.format(ErrorCode.PARAM_IS_NULL_MSG, "configuration"),
                ErrorCode.PARAM_IS_NULL, new ArrayList<String>(Arrays.asList("configuration")));
        }
        CommonUtil.nameNotEmptyValidation(configBase.getNameCh(), configBase.getNameEn());
        checkParamPattern(configBase.getConfiguration());

        Config config = new Config();
        config.setId(CommonUtil.generateId());
        config.setNameCh(null == configBase.getNameCh() ? configBase.getNameEn() : configBase.getNameCh());
        config.setNameEn(null == configBase.getNameEn() ? configBase.getNameCh() : configBase.getNameEn());
        config.setDescriptionCh(
            null == configBase.getDescriptionCh() ? configBase.getDescriptionEn() : configBase.getDescriptionCh());
        config.setDescriptionEn(
            null == configBase.getDescriptionEn() ? configBase.getDescriptionCh() : configBase.getDescriptionEn());
        config.setCreateTime(taskRepository.getCurrentDate());
        config.setConfiguration(configBase.getConfiguration());

        configRepository.insert(config);
        LOGGER.info("create config successfully.");
        return config;
    }

    @Override
    public Config updateConfig(ConfigBase configBase, String id) throws FileNotExistsException {
        if (StringUtils.isNotEmpty(configBase.getConfiguration())) {
            checkParamPattern(configBase.getConfiguration());
        }
        Config configDB = configRepository.queryConfigById(id);
        CommonUtil.checkEntityNotFound(configDB, String.format("this config %s not exists.", id), Constant.CONFIG_ID);

        Config config = new Config();
        BeanUtils.copyProperties(configBase, config);
        config.setId(id);
        configRepository.updateConfig(config);
        LOGGER.info("update config successfully.");
        return configRepository.queryConfigById(id);
    }

    @Override
    public Boolean deleteConfig(String id) {
        List<TestCasePo> testCasePo = testCaseRepository.findByConfigId(id);
        if (!CollectionUtils.isEmpty(testCasePo)) {
            List<String> nameList = testCasePo.stream().map(TestCasePo::getNameEn).collect(Collectors.toList());
            throw new IllegalRequestException(
                String.format(ErrorCode.CONFIG_IS_USED_BY_TEST_CASE_MSG, nameList.toString()),
                ErrorCode.CONFIG_IS_USED_BY_TEST_CASE, new ArrayList<String>(Arrays.asList(nameList.toString())));
        }
        configRepository.deleteConfig(id);
        LOGGER.info("delete config successfully.");
        return true;
    }

    @Override
    public Config queryConfig(String id) throws FileNotExistsException {
        Config config = configRepository.queryConfigById(id);
        CommonUtil.checkEntityNotFound(config, String.format("this config %s not exists.", id), Constant.CONFIG_ID);
        LOGGER.info("query config by id successfully.");
        return config;
    }

    @Override
    public PageResult<Config> queryAllConfigs(int limit, int offset, String locale, String name) {
        String nameCh = Constant.LOCALE_CH.equalsIgnoreCase(locale) ? name : null;
        String nameEn = Constant.LOCALE_EN.equalsIgnoreCase(locale) ? name : null;
        PageResult<Config> pageResult = new PageResult<Config>(offset, limit);
        pageResult.setTotal(configRepository.countTotal(nameCh, nameEn));
        pageResult.setResults(configRepository.getAllWithPagination(limit, offset, nameCh, nameEn));
        LOGGER.info("query all configs successfully.");
        return pageResult;
    }

    /**
     * check configuration param pattern.
     *
     * @param configuration config params list
     */
    private void checkParamPattern(String configuration) {
        String[] params = configuration.split(Constant.SEMICOLON);
        if (0 == params.length) {
            LOGGER.error("config param pattern error, not having ;");
            throw new IllegalRequestException(ErrorCode.CONFIG_PARAM_PATTERN_ERROR_MSG,
                ErrorCode.CONFIG_PARAM_PATTERN_ERROR, null);
        }
        for (String param : params) {
            String[] config = param.split(Constant.EQUAL_MARK);
            // param patter: key = value or value is null, key = ;
            if (!param.contains(Constant.EQUAL_MARK) || 2 != config.length && 1 != config.length) {
                LOGGER.error("config param pattern error,not having =");
                throw new IllegalRequestException(ErrorCode.CONFIG_PARAM_PATTERN_ERROR_MSG,
                    ErrorCode.CONFIG_PARAM_PATTERN_ERROR, null);
            }
        }
    }
}
