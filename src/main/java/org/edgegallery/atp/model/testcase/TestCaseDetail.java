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
}
