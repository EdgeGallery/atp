package org.edgegallery.atp.application.task;

import org.edgegallery.atp.application.testcase.TestCaseHandler;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class TaskExecuter {

    private static TaskExecuter instance = new TaskExecuter();

    private Map<String, Set<String>> taskAndSubTaskMap = new ConcurrentHashMap<String, Set<String>>();

    private Map<String, String> antVirusTaskStatusMap = new ConcurrentHashMap<String, String>();

    private Map<String, String> complianceTaskStatusMap = new ConcurrentHashMap<String, String>();

    private Map<String, String> sandBoxTaskStatusMap = new ConcurrentHashMap<String, String>();

    public static TaskExecuter getInstance() {
        return instance;
    }

    public void addSubTask(TestCaseHandler handler, String taskId, String generateId) {
        if(taskAndSubTaskMap.get(taskId) != null) {
            taskAndSubTaskMap.get(taskId).add(generateId);
        } else {
            Set<String> subTaskSet = new HashSet<>();
            subTaskSet.add(generateId);
            taskAndSubTaskMap.put(taskId, subTaskSet);
        }
        if(TestCaseHandler.TYPE_TEST_CASE_ANTIVIRUS.equals(handler.getType())) {
            antVirusTaskStatusMap.put(generateId, TestCaseHandler.STATUS_WAITING);
        }
        else if(TestCaseHandler.TYPE_TEST_CASE_COMPLIANCE.equals(handler.getType())) {
            complianceTaskStatusMap.put(generateId, TestCaseHandler.STATUS_WAITING);
        }
        else if(TestCaseHandler.TYPE_TEST_CASE_SANDBOX.equals(handler.getType())) {
            sandBoxTaskStatusMap.put(generateId, TestCaseHandler.STATUS_WAITING);
        }
    }
}
