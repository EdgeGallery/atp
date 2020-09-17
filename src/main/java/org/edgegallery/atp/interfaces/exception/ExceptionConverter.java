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

package org.edgegallery.atp.interfaces.exception;

import javax.validation.ConstraintViolationException;

import org.apache.servicecomb.swagger.invocation.Response;
import org.apache.servicecomb.swagger.invocation.SwaggerInvocation;
import org.apache.servicecomb.swagger.invocation.exception.ExceptionToProducerResponseConverter;
import org.edgegallery.atp.utils.exception.EntityNotFoundException;
import org.edgegallery.atp.utils.exception.RedundantCommentsException;
import org.edgegallery.atp.utils.exception.UnAuthorizedExecption;
import org.edgegallery.atp.utils.exception.UnknownReleaseExecption;

public class ExceptionConverter implements ExceptionToProducerResponseConverter<Exception> {
    @Override
    public Class<Exception> getExceptionClass() {
        return Exception.class;
    }

    @Override
    public Response convert(SwaggerInvocation swaggerInvocation, Exception e) {
        if (e instanceof IllegalArgumentException) {
            return Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST);
        }
        if (e instanceof EntityNotFoundException) {
            return Response.status(javax.ws.rs.core.Response.Status.NOT_FOUND);
        }
        if (e instanceof UnknownReleaseExecption) {
            return Response.status(javax.ws.rs.core.Response.Status.NOT_FOUND);
        }
        if (e instanceof UnAuthorizedExecption) {
            return Response.status(javax.ws.rs.core.Response.Status.UNAUTHORIZED);
        }
        if (e instanceof ConstraintViolationException) {
            return Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST);
        }
        if (e instanceof RedundantCommentsException) {
            return Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST);
        }
        return Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST);
    }
}
