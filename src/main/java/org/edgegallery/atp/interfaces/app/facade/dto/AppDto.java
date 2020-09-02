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

package org.edgegallery.atp.interfaces.app.facade.dto;

import lombok.Getter;
import lombok.Setter;
import org.edgegallery.atp.domain.model.app.App;

@Getter
@Setter
public class AppDto {
    private String appId;

    private String iconUrl;

    private String name;

    private String provider;

    private String type;

    private String shortDesc;

    private String createTime;

    private String details;

    private int downloadCount;

    private String affinity;

    private String industry;

    private String contact;

    private double score;

    private String userId;

    private String userName;

    /**
     * Construstor function.
     *
     * @param appId app Id.
     * @param name app name.
     * @param provider app provider.
     * @param type app type.
     * @param shortDesc shore desc of app.
     * @param createTime create time of app.
     * @param details details information of app.
     * @param downloadCount download count of app.
     * @param affinity affinity of app.
     * @param score scored by other users.
     * @param userId user id of app.
     */
    public AppDto(String appId, String affinity, String industry, String contact, String name, String provider,
                  String  type, String shortDesc, String createTime, String details, int downloadCount, double score,
                  String userId, String userName) {
        this.appId = appId;
        this.name = name;
        this.provider = provider;
        this.type = type;
        this.shortDesc = shortDesc;
        this.createTime = createTime;
        this.details = details;
        this.downloadCount = downloadCount;
        this.affinity = affinity;
        this.industry = industry;
        this.contact = contact;
        this.score = score;
        this.userId = userId;
        this.userName = userName;
    }

    /**
     * transfer App to AppDto object..
     * @param app is an App object.
     * @return
     */
    public static AppDto of(App app) {
        return new AppDto(app.getAppId(), app.getAffinity(), app.getIndustry(), app.getContact(), app.getAppName(),
                app.getProvider(), app.getApplicationType(), app.getShortDesc(), app.getCreateTime(),
                app.getAppIntroduction(), app.getDownloadCount(), app.getScore(), app.getUser().getUserId(),
                app.getUser().getUserName());
    }

}
