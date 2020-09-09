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

package org.edgegallery.atp.domain.model.releases;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import org.edgegallery.atp.domain.model.user.User;
import org.edgegallery.atp.domain.shared.ValueObject;
import org.edgegallery.atp.interfaces.app.facade.AppParam;

public class Release implements ValueObject<Release> {

    public static final String PARAM_SHORTDESC_TYPE_SEP = "$sep";

    private String versionId;

    private String appId;

    private final AFile packageFile;

    private AFile icon;

    private String createTime;

    private String shortDesc;

    private String affinity;

    private String industry;

    private String applicationType;

    private User user;

    private BasicInfo appBasicInfo;

    public AFile getPackageFile() {
        return packageFile;
    }

    public String getVersionId() {
        return versionId;
    }

    public AFile getIcon() {
        return icon;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    /**
     * Constructor of Release.
     */
    public Release(AFile packageFile, AFile icon, User user, AppParam appParam) {
        String random = UUID.randomUUID().toString();
        this.versionId = random.replaceAll("-", "");
        this.packageFile = packageFile;
        this.icon = icon;
        this.user = user;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.createTime = simpleDateFormat.format(new Date());
        this.shortDesc = appParam.getShortDesc();
        this.applicationType = appParam.getApplicationType();
        this.industry = appParam.getIndustry();
        this.affinity = appParam.getAffinity();
        appBasicInfo = new BasicInfo().load(packageFile.getStorageAddress());
    }

    /**
     * Constructor of Release.
     */
    public Release(AFile packageFile, String appId, String versionId, AFile icon, String createTime, String shortDesc,
        String affinity, String type, String industry, User user, BasicInfo appBasicInfo) {
        this.packageFile = packageFile;
        this.appId = appId;
        this.versionId = versionId;
        this.icon = icon;
        this.createTime = createTime;
        this.shortDesc = shortDesc;
        this.affinity = affinity;
        this.applicationType = type;
        this.industry = industry;
        this.user = user;
        this.appBasicInfo = appBasicInfo;
    }

    public BasicInfo getAppBasicInfo() {
        return appBasicInfo;
    }

    public void check(String filePath) {
        //TODO
    }

    public String getAppId() {
        return appId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public String getAffinity() {
        return affinity;
    }

    public String getIndustry() {
        return industry;
    }

    public String getApplicationType() {
        return applicationType;
    }

    public User getUser() {
        return user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Release release = (Release) o;
        return Objects.equals(versionId, release.versionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(versionId);
    }

    @Override
    public boolean sameValueAs(Release other) {
        return this.equals(other);
    }
}
