package org.edgegallery.atp.model.testcase;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestCaseDetail {

    List<Map<String, TestCaseResult>> securityTest;

    List<Map<String, TestCaseResult>> complianceTest;

    List<Map<String, TestCaseResult>> sandboxTest;
    
    public TestCaseDetail() {
        
    }
    
    public TestCaseDetail(List<Map<String, TestCaseResult>> securityTest, List<Map<String, TestCaseResult>> complianceTest,List<Map<String, TestCaseResult>> sandboxTest) {
        this.securityTest = securityTest;
        this.complianceTest = complianceTest;
        this.sandboxTest = sandboxTest;
    }
}
