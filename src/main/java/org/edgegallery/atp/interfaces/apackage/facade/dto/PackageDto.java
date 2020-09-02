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

package org.edgegallery.atp.interfaces.apackage.facade.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import org.edgegallery.atp.domain.model.releases.Release;

@Getter
@Setter
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class PackageDto {

    private String csarId;

    private String downloadUrl;

    private String iconUrl;

    private String size;

    private String format;

    private String createTime;

    private String name;

    private String version;

    private String type;

    private String details;

    private String affinity;

    private String industry;

    private String contact;

    private String appId;

    /**
     * Constructor of PackageDto.
     */
    public PackageDto(String csarId, String downloadUrl, String iconUrl, String size, String format, String createTime,
        String name, String version, String type, String details, String affinity, String industry, String contact,
        String appId) {
        this.csarId = csarId;
        this.downloadUrl = downloadUrl;
        this.iconUrl = iconUrl;
        this.size = size;
        this.format = format;
        this.createTime = createTime;
        this.name = name;
        this.version = version;
        this.type = type;
        this.details = details;
        this.affinity = affinity;
        this.industry = industry;
        this.contact = contact;
        this.appId = appId;
    }

    /**
     * Transfer Release object to PackageDto object.
     * @param release Release object.
     * @return
     */
    public static PackageDto of(Release release) {
        return new PackageDto(release.getVersionId(), release.getPackageFile().getStorageAddress(),release.getIcon()
            .getStorageAddress(), release.getPackageFile().getSize(),release.getAppBasicInfo().getFileStructure(),
            release.getCreateTime(), release.getAppBasicInfo().getAppName(),release.getAppBasicInfo().getVersion(),
            release.getApplicationType(),
            release.getAppBasicInfo().getMarkDownContent(), release.getAffinity(),
            release.getIndustry(), release.getAppBasicInfo().getContact(),
            release.getAppId());
    }
}
