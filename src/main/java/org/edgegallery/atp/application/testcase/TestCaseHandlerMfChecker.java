package org.edgegallery.atp.application.testcase;

public class TestCaseHandlerMfChecker extends TestCaseHandler {

    public TestCaseHandlerMfChecker() {
        this.setType(TestCaseHandler.TYPE_TEST_CASE_COMPLIANCE);
    }

    @Override
    public boolean check(String filePath) {
        return true;
    }
}
