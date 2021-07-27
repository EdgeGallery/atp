package org.edgegallery.atp.service;

import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.constant.ErrorCode;
import org.edgegallery.atp.model.config.Config;
import org.edgegallery.atp.model.config.ConfigBase;
import org.edgegallery.atp.repository.config.ConfigRepository;
import org.edgegallery.atp.repository.task.TaskRepository;
import org.edgegallery.atp.utils.CommonUtil;
import org.edgegallery.atp.utils.exception.IllegalRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("ConfigService")
public class ConfigServiceImpl implements ConfigService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigServiceImpl.class);

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    ConfigRepository configRepository;

    @Override
    public Config createConfig(ConfigBase configBase) {
        Config config = new Config();
        CommonUtil.nameExistenceValidation(configBase.getNameCh(), configBase.getNameEn());
        checkParamPattern(configBase.getConfiguration());

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
        //TODO PARAMS validate
        return null;
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
            // param patter: key = value;
            if (2 != config.length) {
                LOGGER.error("config param pattern error,not having =");
                throw new IllegalRequestException(ErrorCode.CONFIG_PARAM_PATTERN_ERROR_MSG,
                    ErrorCode.CONFIG_PARAM_PATTERN_ERROR, null);
            }
        }
    }
}
