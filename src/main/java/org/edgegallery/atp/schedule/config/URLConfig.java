package org.edgegallery.atp.schedule.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * URL address config.
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "serveraddress")
public class URLConfig {
    private String apm;

    private String appo;

    private String inventory;

    private String appstore;
}
