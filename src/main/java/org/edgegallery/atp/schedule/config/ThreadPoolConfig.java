package org.edgegallery.atp.schedule.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Thread pool param config.
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "task.pool")
public class ThreadPoolConfig {
    private int corePoolSize;

    private int maxPoolSize;

    private int keepAliveSeconds;

    private int queueCapacity;
}
