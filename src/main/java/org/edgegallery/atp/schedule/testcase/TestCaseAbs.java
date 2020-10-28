package org.edgegallery.atp.schedule.testcase;

import java.util.Map;
import org.edgegallery.atp.model.testcase.TestCaseResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * test case module class
 *
 */
@Component
public abstract class TestCaseAbs {

    @Value("${applcm.host.ip}")
    public String appoIp;

    @Value("${applcm.host.port}")
    public String appoPort;

    @Value("${applcm.host.protocol}")
    public String appoProtoctol;

    @Value("${inventory.host.ip}")
    public String inventoryIp;

    @Value("${inventory.host.port}")
    public String inventoryPort;

    @Value("${inventory.host.protocol}")
    public String inventoryProtoctol;

    /**
     * execute test case method.
     * 
     * @param filePath filePath
     * @param context context info
     * @return
     */
    public abstract TestCaseResult execute(String filePath, Map<String, String> context);

    /**
     * set return result.
     * 
     * @param result task result
     * @param reason failed reason
     * @param testCaseresult return result model
     * @return return result model
     */
    public TestCaseResult setTestCaseResult(String result, String reason, TestCaseResult testCaseresult) {
        testCaseresult.setReason(reason);
        testCaseresult.setResult(result);
        return testCaseresult;
    }

}
