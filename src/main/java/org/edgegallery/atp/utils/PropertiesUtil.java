package org.edgegallery.atp.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesUtil.class);

    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream inputStream =
                PropertiesUtil.class.getClassLoader().getResourceAsStream("application.properties")) {
            PROPERTIES.load(inputStream);
        } catch (IOException e) {
            LOGGER.error("Failed to read resource file. {}", e.getMessage());
        }
    }

    private PropertiesUtil() {
    }

    public static String getProperties(String key) {
        return PROPERTIES.getProperty(key);
    }
}
