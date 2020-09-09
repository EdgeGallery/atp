package org.edgegallery.atp.application.testcase;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestCaseHandler {

    public static final String TYPE_TEST_CASE_ANTIVIRUS = "Antivirus";

    public static final String TYPE_TEST_CASE_COMPLIANCE = "Compliance";

    public static final String TYPE_TEST_CASE_SANDBOX = "SandBox";

    public static final String STATUS_RUNNING = "running";

    public static final String STATUS_WAITING = "waiting";

    public static final String STATUS_FAILED = "failed";

    public static final String STATUS_PASS = "pass";

    private String testCaseId;

    private String testCaseName;

    private String result;

    private String type;

    public boolean check(String filePath) {
        return true;
    }
}
