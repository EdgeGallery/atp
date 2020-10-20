package org.edgegallery.atp.model.testcase;

import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestCaseQueryRes {

    List<Map<String, String>> virusScanningTest;

    List<Map<String, String>> complianceTest;

    List<Map<String, String>> sandboxTest;

    public TestCaseQueryRes(List<Map<String, String>> virusScanningTest, List<Map<String, String>> complianceTest,
            List<Map<String, String>> sandboxTest) {
        this.virusScanningTest = virusScanningTest;
        this.complianceTest = complianceTest;
        this.sandboxTest = sandboxTest;
    }
}
