package org.edgegallery.atp.utils;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(JSONUtil.class);

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * transfer object to String.
     * 
     * @param obj obj
     * @return String type variable
     * @throws IOException
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
     * @param <T>
     * @param src souce String
     * @param type target type
     * @return target type model
     * @throws IOException
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
