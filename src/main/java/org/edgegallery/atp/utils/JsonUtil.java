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

package org.edgegallery.atp.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtil.class);

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonUtil() {

    }

    /**
     * transfer object to String.
     * 
     * @param obj obj
     * @return String type variable
     * @throws IOException IOException
     */
    public static String marshal(Object obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            LOGGER.error("marshal obj failed. {}", obj);
            throw new IllegalArgumentException("marshal obj failed.");
        }
    }

    /**
     * transfer String type to special model type.
     * 
     * @param <T> class
     * @param src src
     * @param type type
     * @return class
     */
    public static <T> T unMarshal(String src, Class<T> type) {
        String msg = "unmarshal obj failed: ";
        try {
            return MAPPER.readValue(src, type);
        } catch (IOException e) {
            LOGGER.error(msg + src);
            throw new IllegalArgumentException(msg);
        }
    }

}
