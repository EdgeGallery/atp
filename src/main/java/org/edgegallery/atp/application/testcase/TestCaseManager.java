package org.edgegallery.atp.application.testcase;

import java.util.List;
import java.util.ArrayList;

public class TestCaseManager {

    private static TestCaseManager instance = new TestCaseManager();

    private List<TestCaseHandler> testCaseHandlers = new ArrayList<>();

    public TestCaseManager() {
        TestCaseHandler mfHandler = new TestCaseHandlerMfChecker();
        testCaseHandlers.add(mfHandler);
    }

    public static TestCaseManager getInstance() {
        return instance;
    }

    public List<TestCaseHandler> getAllTestCases() {
        return testCaseHandlers;
    }
//
//    public List<TestCase> getAllTestCasesExecutor() {
//        return testCaseList;
//    }
}
