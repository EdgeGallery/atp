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

package org.edgegallery.atp.infrastructure.persistence.app;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.edgegallery.atp.domain.model.app.App;
import org.edgegallery.atp.domain.model.user.User;
import org.edgegallery.atp.infrastructure.persistence.PersistenceObject;



@Getter
@Setter
@Entity
@Table(name = "app_table")
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppPO
        implements PersistenceObject<App> {

    public static final String APP_NAME = "appName";

    @Id
    @Column(name = "APPID")
    private String appId;

    @Column(name = "APPNAME")
    private String appName;

    @Column(name = "APPLICATIONTYPE")
    private String applicationType; //applicationType

    @Column(name = "SHORTDESC")
    private String shortDesc; //

    @Column(name = "PROVIDER")
    private String provider; //

    @Column(name = "APPINTRODUCTION")
    private String appIntroduction;

    @Column(name = "DOWNLOADCOUNT")
    private int downloadCount;

    @Column(name = "AFFINITY")
    private String affinity;

    @Column(name = "INDUSTRY")
    private String industry;

    @Column(name = "CONTACT")
    private String contact;

    @Column(name = "USERID")
    private String userId;

    @Column(name = "USERNAME")
    private String userName;

    @Column(name = "CREATETIME")
    private String createTime;

    @Column(name = "MODIFYTIME")
    private String modifyTime;

    @Column(name = "SCORE")
    private double score;


    @Override
    public App toDomainModel() {
        return new App(appId, appName, provider, createTime, modifyTime, downloadCount,
                score, shortDesc, affinity, industry, contact, applicationType, appIntroduction, new User(userId,
            userName));
    }

    public AppPO(){
    }

    /**
     * Constructor of AppPO.
     */
    public AppPO(String appId, String appName, String applicationType, String shortDesc, String provider,
                 String appIntroduction, int downloadCount, User user, String createTime, String modifyTime,
                 String affinity, String industry, String contact, double score) {
        this.appId = appId;
        this.appName = appName;
        this.applicationType = applicationType;
        this.shortDesc = shortDesc;
        this.provider = provider;
        this.appIntroduction = appIntroduction;
        this.downloadCount = downloadCount;
        this.userId = user.getUserId();
        this.userName = user.getUserName();
        this.createTime = createTime;
        this.modifyTime = modifyTime;
        this.affinity = affinity;
        this.industry = industry;
        this.contact = contact;
        this.score = score;
    }

    static AppPO of(App app) {
        return new AppPO(app.getAppId(), app.getAppName(), app.getApplicationType(), app.getShortDesc(),
            app.getProvider(),
                app.getAppIntroduction(), app.getDownloadCount(), app.getUser(), app.getCreateTime(),
            app.getUpdateTime(),
                app.getAffinity(), app.getIndustry(), app.getContact(), app.getScore());
    }
}
