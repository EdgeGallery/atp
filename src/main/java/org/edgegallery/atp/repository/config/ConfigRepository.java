package org.edgegallery.atp.repository.config;

import org.edgegallery.atp.model.config.Config;

public interface ConfigRepository {
    /**
     * insert config into db.
     *
     * @param config config info
     */
    void insert(Config config);
}
