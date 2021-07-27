package org.edgegallery.atp.service;

import org.edgegallery.atp.model.config.Config;
import org.edgegallery.atp.model.config.ConfigBase;

public interface ConfigService {
    /**
     * create a config.
     *
     * @param config config info
     * @return config info
     */
    Config createConfig(ConfigBase config);
}
