package org.edgegallery.atp.repository.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface BatchTaskMapper {

    /**
     * get batch task id by batch taskId and userId
     * 
     * @param taskId
     * @param userId
     * @return
     */
    String findBatchTask(String id, String userId);
}
