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

package org.edgegallery.atp.model.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import org.apache.commons.collections4.CollectionUtils;
import org.edgegallery.atp.model.task.testscenarios.TaskTestCase;
import org.edgegallery.atp.model.task.testscenarios.TaskTestCasePo;
import org.edgegallery.atp.model.task.testscenarios.TaskTestScenarioPo;
import org.edgegallery.atp.model.task.testscenarios.TaskTestSuite;
import org.edgegallery.atp.model.task.testscenarios.TaskTestSuitePo;
import org.edgegallery.atp.utils.JSONUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskPO {

    @Column(name = "id")
    private String id;

    @Column(name = "appName")
    private String appName;

    @Column(name = "appVersion")
    private String appVersion;

    @Column(name = "status")
    private String status;

    @Column(name = "createTime")
    private Date createTime;

    @Column(name = "endTime")
    private Date endTime;

    @Column(name = "userId")
    private String userId;

    @Column(name = "userName")
    private String userName;

    @Column(name = "testCaseDetail")
    private String testCaseDetail;

    @Column(name = "providerId")
    private String providerId;

    @Column(name = "packagePath")
    private String packagePath;

    public static TaskPO of(TaskRequest startTest) {
        TaskPO taskPo = new TaskPO();
        taskPo.setAppName(startTest.getAppName());
        taskPo.setAppVersion(startTest.getAppVersion());
        taskPo.setCreateTime(startTest.getCreateTime());
        taskPo.setEndTime(startTest.getEndTime());
        taskPo.setId(startTest.getId());
        taskPo.setStatus(startTest.getStatus());
        taskPo.setUserId(startTest.getUser().getUserId());
        taskPo.setUserName(startTest.getUser().getUserName());
        List<TaskTestScenarioPo> taskTestScenarioPoList = new ArrayList<TaskTestScenarioPo>(); 
        if(CollectionUtils.isNotEmpty(startTest.getTestScenarios())) {
            startTest.getTestScenarios().forEach(taskTestScenario->{
                TaskTestScenarioPo scenarioPo = taskTestScenario.of();
                taskTestScenarioPoList.add(scenarioPo);
                List<TaskTestSuitePo> testSuites = new ArrayList<TaskTestSuitePo>();
                List<TaskTestSuite> taskTestSuite = taskTestScenario.getTestSuites();
                if(CollectionUtils.isNotEmpty(taskTestSuite)) {
                    taskTestSuite.forEach(testSuite->{
                        TaskTestSuitePo suitePo = testSuite.of();
                        testSuites.add(suitePo);
                        List<TaskTestCasePo> testCasePo = new ArrayList<TaskTestCasePo>();
                        List<TaskTestCase> testCases = testSuite.getTestCases();
                        if(CollectionUtils.isNotEmpty(testCases)) {
                            testCases.forEach(testCase->{
                                testCasePo.add(testCase.of());
                            });
                            suitePo.setTestCases(testCasePo); 
                        }
                    });
                    scenarioPo.setTestSuites(testSuites); 
                }
            }); 
        }
        taskPo.setTestCaseDetail(JSONUtil.marshal(taskTestScenarioPoList));
        taskPo.setPackagePath(startTest.getPackagePath());
        taskPo.setProviderId(startTest.getProviderId());

        return taskPo;
    }
    
}
