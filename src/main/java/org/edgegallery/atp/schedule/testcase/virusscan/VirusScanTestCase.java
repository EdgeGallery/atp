package org.edgegallery.atp.schedule.testcase.virusscan;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Map;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.constant.ExceptionConstant;
import org.edgegallery.atp.model.testcase.TestCaseResult;
import org.edgegallery.atp.schedule.testcase.TestCaseAbs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * execute virus scan class.
 *
 */
public class VirusScanTestCase extends TestCaseAbs {

    private static final Logger LOGGER = LoggerFactory.getLogger(VirusScanTestCase.class);

    private TestCaseResult testCaseResult = new TestCaseResult();

    private static final String PATH_COMMAND = "cd /usr/app/clamav-0.102.4";

    private static final String EXECUTE_SCAN_COMMAND = "clamscan %s";

    private static final String EXIT_COMMAND = "exit";

    @Override
    public TestCaseResult execute(String filePath, Map<String, String> context) {
        LOGGER.warn("virus scan in.");
        try {
            Process proc = Runtime.getRuntime().exec("/bin/bash", null, new File("/bin"));
            LOGGER.warn("proc get.");
            if (null != proc) {
                LOGGER.warn("proc is not null.");
                try (BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                        PrintWriter out = new PrintWriter(
                                new BufferedWriter(new OutputStreamWriter(proc.getOutputStream())), true)) {
                    out.println(PATH_COMMAND);
                    out.println(String.format(EXECUTE_SCAN_COMMAND, filePath));
                    out.println(EXIT_COMMAND);

                    String line = "";
                    LOGGER.warn("line init.");
                    while ((line = in.readLine()) != null) {
                        LOGGER.warn("virus scan line: {}", line);
                        if (line.startsWith("Infected files")) {
                            String scanResult = line.split(": ")[1].trim();
                            LOGGER.warn("virus scan Infected files: {}", scanResult);
                            return "0".equals(scanResult)
                                    ? setTestCaseResult(Constant.SUCCESS, Constant.EMPTY, testCaseResult)
                                    : setTestCaseResult(Constant.FAILED,
                                            String.format(ExceptionConstant.FIND_VIRUS, scanResult), testCaseResult);
                        }
                    }
                } finally {
                    proc.destroy();
                }
            }

        } catch (IOException e) {
            LOGGER.error("Failed to execute virus scan, exception is {}", e.getMessage());
        }
        return setTestCaseResult(Constant.FAILED, ExceptionConstant.INNER_EXCEPTION, testCaseResult);
    }

}
