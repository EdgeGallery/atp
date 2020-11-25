package org.edgegallery.atp.interfaces.filter;

/**
 * Copyright 2020 Huawei Technologies Co.,Ltd.**Licensed under the Apache License,Version
 * 2.0(the"License");*you may not use this file except in compliance with the License.*You may
 * obtain a copy of the License at**http:// www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing,software*distributed under the License is distributed on
 * an"AS IS"BASIS,*WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,either express or implied.*See the
 * License for the specific language governing permissions and*limitations under the License.
 */

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.constant.ExceptionConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerTokenServicesConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Import({ResourceServerTokenServicesConfiguration.class})
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AccessTokenFilter extends OncePerRequestFilter {

    @Autowired
    TokenStore jwtTokenStore;

    public static ThreadLocal<Map<String, String>> context = new ThreadLocal<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Map<String, String> contextMap = new HashMap<>();
        String accessTokenStr = request.getHeader(Constant.ACCESS_TOKEN);
        if (StringUtils.isEmpty(accessTokenStr)) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Access token is empty");
            return;
        }
        OAuth2AccessToken accessToken = jwtTokenStore.readAccessToken(accessTokenStr);
        if (accessToken == null) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), ExceptionConstant.INVALID_ACCESS_TOKEN);
            return;
        }
        if (accessToken.isExpired()) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Access token expired");
            return;
        }
        Map<String, Object> additionalInfoMap = accessToken.getAdditionalInformation();
        if (additionalInfoMap == null) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), ExceptionConstant.INVALID_ACCESS_TOKEN);
            return;
        }
        String userIdFromRequest = request.getParameter(Constant.USER_ID);
        String userIdFromToken = additionalInfoMap.get(Constant.USER_ID).toString();
        if (!StringUtils.isEmpty(userIdFromRequest) && !userIdFromRequest.equals(userIdFromToken)) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Illegal userId");
            return;
        }
        String userNameFromRequest = request.getParameter(Constant.USER_NAME);
        String userNameFromToken = additionalInfoMap.get(Constant.USER_NAME).toString();
        if (!StringUtils.isEmpty(userNameFromRequest) && !userNameFromRequest.equals(userNameFromToken)) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Illegal userName");
            return;
        }
        OAuth2Authentication auth = jwtTokenStore.readAuthentication(accessToken);
        if (auth == null) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), ExceptionConstant.INVALID_ACCESS_TOKEN);
            return;
        }
        contextMap.put(Constant.ACCESS_TOKEN, accessTokenStr);
        contextMap.put(Constant.USER_ID, userIdFromToken);
        contextMap.put(Constant.USER_NAME, userNameFromToken);
        context.set(contextMap);

        SecurityContextHolder.getContext().setAuthentication(auth);
        filterChain.doFilter(request, response);
    }
}
