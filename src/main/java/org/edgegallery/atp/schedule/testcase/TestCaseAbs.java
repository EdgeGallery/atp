package org.edgegallery.atp.schedule.testcase;

import java.util.Map;
import org.edgegallery.atp.model.testcase.TestCaseResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public abstract class TestCaseAbs {

    @Value("${applcm.host.ip}")
    public String applcmIp;

    @Value("${applcm.host.port}")
    public String applcmPort;

    @Value("${applcm.host.protocol}")
    public String applcmProtoctol;

    @Value("${inventory.host.ip}")
    public String inventoryIp;

    @Value("${inventory.host.port}")
    public String inventoryPort;

    @Value("${inventory.host.protocol}")
    public String inventoryProtoctol;

    public abstract TestCaseResult execute(String filePath, Map<String, String> context);

    public TestCaseResult setTestCaseResult(String result, String reason, TestCaseResult testCaseresult) {
        testCaseresult.setReason(reason);
        testCaseresult.setResult(result);
        return testCaseresult;
    }

}
