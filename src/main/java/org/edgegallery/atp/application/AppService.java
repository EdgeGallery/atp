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

package org.edgegallery.atp.application;

import java.util.Optional;
import org.edgegallery.atp.domain.model.app.App;
import org.edgegallery.atp.domain.model.app.AppRepository;
import org.edgegallery.atp.domain.model.comment.CommentRepository;
import org.edgegallery.atp.domain.model.releases.Release;
import org.edgegallery.atp.domain.model.releases.UnknownReleaseExecption;
import org.edgegallery.atp.domain.model.user.User;
import org.edgegallery.atp.domain.service.FileService;
import org.edgegallery.atp.domain.shared.exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("AppRegisterService")
public class AppService {

    @Autowired
    private AppRepository appRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private FileService fileService;

    public Release getRelease(String appId, String packageId) {
        App app = appRepository.find(appId).orElseThrow(() -> new EntityNotFoundException(App.class, appId));
        return app.findByVersion(packageId).orElseThrow(() -> new UnknownReleaseExecption(packageId));
    }

    /**
     * register app.
     *
     * @param release use object of release to register.
     */
    @Transactional
    public void registerApp(Release release) {

        Optional<App> existedApp = appRepository.findByAppName(release.getAppBasicInfo().getAppName());
        App app = null;
        if (existedApp.isPresent()) {
            app = existedApp.get();
            app.upload(release);
        } else {
            String appId = appRepository.generateAppId();
            app = new App(appId, release);
        }
        release.setAppId(app.getAppId());
        appRepository.store(app);
    }

    /**
     * delete package by app id and package id.
     * @param appId app id
     * @param packageId package id
     * @param user obj of User
     */
    public void unPublishPackage(String appId, String packageId, User user) {
        App app = appRepository.find(appId).orElseThrow(() -> new EntityNotFoundException(App.class, appId));
        Release release = app.findByVersion(packageId).orElseThrow(() -> new UnknownReleaseExecption(packageId));
        app.unPublish(release);
        appRepository.store(app);
    }

    /**
     * download package by app id and package id.
     * @param appId app id.
     * @param packageId package id.
     * @return
     */
    public Release download(String appId, String packageId) {
        App app = appRepository.find(appId).orElseThrow(() -> new EntityNotFoundException(App.class, appId));
        Release release = app.findByVersion(packageId).orElseThrow(() -> new UnknownReleaseExecption(packageId));
        app.downLoad();
        appRepository.store(app);
        return release;
    }

    /**
     * unPublish app.
     *
     * @param app app object.
     */
    @Transactional
    public void unPublish(App app) {
        app.getReleases().forEach(it -> {
            fileService.delete(it.getIcon());
            fileService.delete(it.getPackageFile());
        });
        appRepository.remove(app.getAppId());
        commentRepository.removeByAppId(app.getAppId());
    }
}
