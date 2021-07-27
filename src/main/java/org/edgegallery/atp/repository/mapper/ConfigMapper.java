package org.edgegallery.atp.repository.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.edgegallery.atp.model.config.Config;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface ConfigMapper {
    /**
     * insert config into db.
     *
     * @param config config info
     */
    void insert(Config config);
}
