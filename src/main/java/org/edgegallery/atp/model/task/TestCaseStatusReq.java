package org.edgegallery.atp.model.task;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestCaseStatusReq {
    private String testScenarioId;
    private String testSuiteId;
    private String testCaseId;
    private String result;
    private String reason;
}
