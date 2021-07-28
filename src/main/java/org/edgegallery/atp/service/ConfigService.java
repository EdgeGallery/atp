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

import org.edgegallery.atp.model.PageResult;
import org.edgegallery.atp.model.config.Config;
import org.edgegallery.atp.model.config.ConfigBase;
import org.edgegallery.atp.utils.exception.FileNotExistsException;

public interface ConfigService {
    /**
     * create a config.
     *
     * @param config config info
     * @return config info
     */
    Config createConfig(ConfigBase config);

    /**
     * update a config.
     *
     * @param config config info
     * @param id config id
     * @return config info
     */
    Config updateConfig(ConfigBase config, String id) throws FileNotExistsException;

    /**
     * delete a config.
     *
     * @param id config id
     * @return true
     */
    Boolean deleteConfig(String id);

    /**
     * query config by id.
     *
     * @param id config id
     * @return config info
     */
    Config queryConfig(String id) throws FileNotExistsException;

    /**
     * query all configs.
     *
     * @param limit limit
     * @param offset offset
     * @param locale locale
     * @param name name
     * @return config info with page pattern
     */
    PageResult<Config> queryAllConfigs(int limit, int offset, String locale, String name);
}
