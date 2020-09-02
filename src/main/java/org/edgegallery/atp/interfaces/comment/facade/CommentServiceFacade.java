/*
 *    Copyright 2020 Huawei Technologies Co., Ltd.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.edgegallery.atp.interfaces.comment.facade;

import java.util.List;
import org.edgegallery.atp.application.AppCommentService;
import org.edgegallery.atp.domain.model.comment.Comment;
import org.edgegallery.atp.domain.model.comment.CommentRepository;
import org.edgegallery.atp.domain.model.user.User;
import org.edgegallery.atp.domain.shared.PageCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service("CommentServiceFacade")
public class CommentServiceFacade {

    public static final Logger LOGGER = LoggerFactory.getLogger(CommentServiceFacade.class);

    private static final int MAX_RESPONSE_BODY = 100;

    @Autowired
    AppCommentService appCommentService;

    @Autowired
    CommentRepository commentRepository;

    /**
     * comment method with parameters.
     */
    public void comment(User user, String appId, String comments, double score) {
        String traceComment = "";
        if (comments.length() > MAX_RESPONSE_BODY) {
            traceComment = comments.substring(0, MAX_RESPONSE_BODY) + "...";
        } else {
            traceComment = comments;
        }
        LOGGER.info("User {} comments {} to app {}, score{}", user.getUserName(), traceComment, score);
        appCommentService.comment(user, appId, comments, score);
    }

    public ResponseEntity<List<Comment>> getComments(String appId, int limit, long offset) {
        return ResponseEntity.ok(commentRepository.findAllWithPagination(new PageCriteria(limit, offset, appId))
            .getResults());
    }
}
