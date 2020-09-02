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
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.edgegallery.atp.domain.model.releases.AFile;
import org.edgegallery.atp.domain.model.releases.BasicInfo;
import org.edgegallery.atp.domain.model.releases.Release;
import org.edgegallery.atp.domain.model.user.User;

@Getter
@Setter
@Entity
@Table(name = "catalog_package_table")
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppReleasePO {
    @Id
    @Column(name = "versionID")
    private String versionID;

    @Column(name = "packageAddress")
    private String packageAddress; //packageAddress

    @Column(name = "iconAddress")
    private String iconAddress; //iconAddress

    @Column(name = "SIZE")
    private String size;

    @Column(name = "fileStructure")
    private String fileStructure; //Tree

    @Column(name = "CREATETIME")
    private String createTime;

    @Column(name = "SHORTDESC")
    private String shortDesc;

    @Column(name = "appName")
    private String appName;

    @Column(name = "VERSION")
    private String version;

    @Column(name = "PROVIDER")
    private String provider;

    @Column(name = "applicationType")
    private String applicationType; //applicationType

    @Column(name = "markdownContent")
    private String markDownContent;

    @Column(name = "AFFINITY")
    private String affinity;

    @Column(name = "INDUSTRY")
    private String industry;

    @Column(name = "CONTACT")
    private String contact;

    @Column(name = "APPID")
    private String appId;

    @Column(name = "USERID")
    private String userId;

    @Column(name = "USERNAME")
    private String userName;

    public AppReleasePO() {
    }

    /**
     * Constructor of AppReleasePO.
     */
    public AppReleasePO(String versionId, String downloadUrl, String iconUrl, String size, String format,
        String createTime, String shortDesc, String name, String version, String type, String details, String affinity,
        String industry, String contact, String appId, String userId, String userName, String provider) {
        this.versionID = versionId;
        this.packageAddress = downloadUrl;
        this.iconAddress = iconUrl;
        this.size = size;
        this.fileStructure = format;
        this.createTime = createTime;
        this.shortDesc = shortDesc;
        this.appName = name;
        this.version = version;
        this.applicationType = type;
        this.markDownContent = details;
        this.affinity = affinity;
        this.industry = industry;
        this.contact = contact;
        this.appId = appId;
        this.userId = userId;
        this.userName = userName;
        this.provider = provider;
    }

    /**
     * transfer Release to AppRelease.
     *
     * @param pack object of Release.
     * @return
     */
    public static AppReleasePO of(Release pack) {
        return new AppReleasePO(pack.getVersionId(), pack.getPackageFile().getStorageAddress(),
            pack.getIcon().getStorageAddress(), pack.getPackageFile().getSize(),
            pack.getAppBasicInfo().getFileStructure(), pack.getCreateTime(), pack.getShortDesc(),
            pack.getAppBasicInfo().getAppName(), pack.getAppBasicInfo().getVersion(), pack.getApplicationType(),
            pack.getAppBasicInfo().getMarkDownContent(), pack.getAffinity(), pack.getIndustry(),
            pack.getAppBasicInfo().getContact(), pack.getAppId(),
            pack.getUser().getUserId(), pack.getUser().getUserName(), pack.getAppBasicInfo().getProvider());
    }

    /**
     * transfer to Release.
     *
     * @return
     */
    public Release toDomainModel() {
        BasicInfo basicInfo = new BasicInfo().load(packageAddress);
        return new Release(new AFile(packageAddress, packageAddress), appId, versionID,
            new AFile(iconAddress, iconAddress), createTime, shortDesc, affinity, applicationType, industry,
            new User(userId, userName),
            basicInfo);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AppReleasePO that = (AppReleasePO) o;
        return Objects.equals(versionID, that.versionID) && Objects.equals(packageAddress, that.packageAddress)
            && Objects.equals(iconAddress, that.iconAddress) && Objects.equals(size, that.size) && Objects.equals(
            fileStructure, that.fileStructure) && Objects.equals(createTime, that.createTime) && Objects.equals(
            shortDesc, that.shortDesc) && Objects.equals(appName, that.appName) && Objects.equals(version, that.version)
            && Objects.equals(applicationType, that.applicationType) && Objects.equals(markDownContent,
            that.markDownContent) && Objects.equals(affinity, that.affinity) && Objects.equals(appId, that.appId)
            && Objects.equals(userId, that.userId) && Objects.equals(userName, that.userName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(versionID, packageAddress, iconAddress, size, fileStructure, createTime, shortDesc, appName,
            version, applicationType, markDownContent, affinity, appId, userId, userName);
    }
}

