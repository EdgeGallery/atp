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

package org.edgegallery.atp.domain.model.app;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.edgegallery.atp.domain.model.comment.Comment;
import org.edgegallery.atp.domain.model.releases.Release;
import org.edgegallery.atp.domain.model.user.User;
import org.edgegallery.atp.domain.shared.Entity;

public class App implements Entity {

    private String appId;

    private String appName;

    private String provider;

    private String createTime;

    private String updateTime;

    private int downloadCount;

    private double score;

    private String shortDesc;

    private String affinity;

    private String industry;

    private String contact;

    private String applicationType;

    private String appIntroduction;

    private User user;

    private int numOfcomment;

    private List<Release> releases;

    /**
     * Constructor of App.
     *
     * @param appId id of app.
     * @param release release of app.
     */
    public App(String appId, Release release) {
        this.appId = appId;
        this.appName = release.getAppBasicInfo().getAppName();
        this.shortDesc = release.getShortDesc();
        this.provider = release.getAppBasicInfo().getProvider();
        this.user = release.getUser();
        this.affinity = release.getAffinity();
        this.applicationType = release.getApplicationType();
        this.appIntroduction = release.getAppBasicInfo().getMarkDownContent();
        this.industry = release.getIndustry();
        this.contact = release.getAppBasicInfo().getContact();
        this.releases = Collections.singletonList(release);
    }

    /**
     * Constructor of App.
     *
     * @param appId id of app.
     * @param appName name of app.
     * @param provider provider of app.
     * @param createTime create time of app.
     * @param updateTime app update time.
     * @param downloadCount download count of app.
     * @param score score of app.
     * @param shortDesc short desc of app.
     * @param affinity affinity of app.
     * @param industry industry of app.
     * @param contact email of app.
     * @param applicationType app type.
     * @param appIntroduction Introduction of app.
     * @param user user object.
     */
    public App(String appId, String appName, String provider, String createTime, String updateTime, int downloadCount,
        double score, String shortDesc, String affinity, String industry, String contact, String applicationType,
        String appIntroduction,
        User user) {
        this.appId = appId;
        this.appName = appName;
        this.provider = provider;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.downloadCount = downloadCount;
        this.score = score;
        this.shortDesc = shortDesc;
        this.affinity = affinity;
        this.industry = industry;
        this.contact = contact;
        this.applicationType = applicationType;
        this.appIntroduction = appIntroduction;
        this.user = user;
    }

    public String getAppName() {
        return appName;
    }

    public String getProvider() {
        return provider;
    }

    public String getCreateTime() {
        return createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public int getDownloadCount() {
        return downloadCount;
    }

    public double getScore() {
        return score;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public String getAffinity() {
        return affinity;
    }

    public String getApplicationType() {
        return applicationType;
    }

    public String getAppIntroduction() {
        return appIntroduction;
    }

    public String getUserId() {
        return user.getUserId();
    }

    public List<Release> getReleases() {
        return releases;
    }

    public void setReleases(List<Release> releases) {
        this.releases = releases;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    /**
     * upload function.
     *
     * @param release app release.
     */
    public void upload(Release release) {
        if (!provider.equals(release.getAppBasicInfo().getProvider())) {
            throw new IllegalArgumentException();
        }
        this.shortDesc = release.getShortDesc();
        this.affinity = release.getAffinity();
        this.contact = release.getAppBasicInfo().getContact();
        this.applicationType = release.getApplicationType();
        this.appIntroduction = release.getAppBasicInfo().getMarkDownContent();
        releases.add(release);
    }

    public String getAppId() {
        return appId;
    }

    public void downLoad() {
        downloadCount = downloadCount + 1;
    }

    public void comment(Comment comment) {
        score = (numOfcomment * score + comment.getScore()) / (numOfcomment + 1);
        numOfcomment = numOfcomment + 1;
    }

    public Optional<Release> findByVersion(String packageId) {
        return releases.stream().filter(it -> it.getVersionId().equals(packageId)).findAny();
    }

    public void unPublish(Release release) {
        releases.remove(release);
    }

    public Optional<Release> findLastRelease() {
        return releases.stream().max(Comparator.comparing(Release::getCreateTime));
    }

    public User getUser() {
        return user;
    }
}
