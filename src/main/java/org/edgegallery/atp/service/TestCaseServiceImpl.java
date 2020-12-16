package org.edgegallery.atp.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.model.testcase.TestCase;
import org.edgegallery.atp.repository.testcase.TestCaseRepository;
import org.edgegallery.atp.utils.file.FileChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service("TestCaseService")
public class TestCaseServiceImpl implements TestCaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestCaseServiceImpl.class);

    private StringBuilder BASIC_PATH = new StringBuilder().append(FileChecker.getDir()).append(File.separator)
            .append("testCase").append(File.separator);

    @Autowired
    TestCaseRepository testCaseRepository;

    @Override
    public ResponseEntity<List<TestCase>> getAllTestCases(String type, String name, String verificationModel) {
        if (StringUtils.isNotEmpty(verificationModel)) {
            String[] vfc = verificationModel.split(Constant.COMMA);
            List<TestCase> response = testCaseRepository.findAllTestCases(type, name, vfc[0]);
            for (int i = 1; i < vfc.length; i++) {
                List<TestCase> filter = testCaseRepository.findAllTestCases(type, name, vfc[i]);
                List<TestCase> result = new ArrayList<TestCase>();
                filter.forEach(testCase -> {
                    for (TestCase rsp : response) {
                        if (rsp.getId().equals(testCase.getId())) {
                            result.add(testCase);
                            break;
                        }
                    }
                });
                response.clear();
                response.addAll(result);
            }
            LOGGER.info("get all test cases successfully.");
            return ResponseEntity.ok(response);
        } else {
            LOGGER.info("get all test cases successfully.");
            List<TestCase> response = testCaseRepository.findAllTestCases(type, name, verificationModel);
            return ResponseEntity.ok(response);
        }
    }

    @Override
    public TestCase createTestCase(MultipartFile file, TestCase testCase) {
        if (null != testCaseRepository.findByName(testCase.getName())) {
            throw new IllegalArgumentException("the file name has already exists.");
        }

        String filePath =
                BASIC_PATH.append(testCase.getName()).append(Constant.UNDER_LINE).append(testCase.getId()).toString();
        try {
            FileChecker.createFile(filePath);
            File result = new File(filePath);
            file.transferTo(result);
            testCase.setFilePath(filePath);

            if (Constant.JAVA.equals(testCase.getCodeLanguage())) {
                testCase.setClassName(getClassPath(result));
            }
            testCaseRepository.insert(testCase);
        } catch (IOException e) {
            LOGGER.error("create file failed, test case name is: {}", testCase.getName());
            throw new IllegalArgumentException("create file failed.");
        }
        LOGGER.info("create test case successfully.");
        return testCase;
    }

    @Override
    public TestCase updateTestCase(MultipartFile file, TestCase testCase) {
        TestCase dbData = testCaseRepository.getTestCaseById(testCase.getId());
        if (null == dbData) {
            LOGGER.error("this test case {} not exists.", testCase.getId());
            throw new IllegalArgumentException("this test case not exists.");
        }

        try {
            if (StringUtils.isNotEmpty(file.getOriginalFilename())) {
                String filePath = dbData.getFilePath();
                File result = new File(filePath);
                file.transferTo(result);
                if (Constant.JAVA.equals(testCase.getCodeLanguage())) {
                    testCase.setClassName(getClassPath(result));
                }
            }

            testCaseRepository.update(testCase);
        } catch (IOException e) {
            LOGGER.error("transfer file content failed.{}", e.getMessage());
            throw new IllegalArgumentException("update file failed.");
        }
        return testCaseRepository.getTestCaseById(testCase.getId());
    }

    @Override
    public Boolean deleteTestCase(String id) {
        TestCase testCase = testCaseRepository.getTestCaseById(id);
        if (null != testCase) {
            String filePath = testCase.getFilePath();
            testCaseRepository.delete(id);
            new File(filePath).delete();
        }
        return true;
    }

    @Override
    public TestCase getTestCase(String id) {
        return testCaseRepository.getTestCaseById(id);
    }

    private String getClassPath(File file) {
        StringBuffer classPath = new StringBuffer();
        String className = Constant.EMPTY;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = "";
            while (((line = reader.readLine()) != null)) {
                if (line.startsWith("public class")) {
                    String[] arr = line.split("\\s+");
                    className = arr[2];
                    LOGGER.info("className: {}", className);
                    break;
                }
            }

            return className;
        } catch (IOException e) {
            LOGGER.warn("get class path failed.");
            throw new IllegalArgumentException("get class path failed.");
        }
    }
}
