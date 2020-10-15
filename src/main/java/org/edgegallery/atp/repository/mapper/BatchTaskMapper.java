package org.edgegallery.atp.repository.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.edgegallery.atp.model.task.BatchTask;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface BatchTaskMapper {

    /**
     * get batch task id by batch taskId and userId.
     * 
     * @param taskId
     * @param userId
     * @return
     */
    String findBatchTask(@Param("id") String id, @Param("userId") String userId);

    /**
     * insert into batch task table.
     * 
     * @param batchTask batch task info.
     */
    void insert(BatchTask batchTask);
}
