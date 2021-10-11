/*
 * Copyright 2021 Huawei Technologies Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Map;

/**
 * vulnerable scanning.
 */
public class VulnerableScanningValidation {

    private static final String VULNERABLE_SCANNING_FAILED = "There has vulnerables in mec host.";

    private static final String MEC_HOST_IS_EMPTY = "app instantiate ip not found. ";

    private static final String INNER_EXCEPTION = "inner exception, please check the log.";

    private static final String VULNERABLE = "VULNERABLE";

    private static final String SUCCESS = "success";

    /**
     * execute test case.
     *
     * @param filePath csar file path
     * @param context context info
     * @return execute result
     */
    public String execute(String filePath, Map<String, String> context) {
        String mecHost = getMecHostAppInstantiated(context);
        if (null == mecHost || "".equals(mecHost)) {
            return MEC_HOST_IS_EMPTY;
        }
        try {
            Process proc = Runtime.getRuntime().exec("/bin/bash", null, new File("/bin"));
            if (null != proc) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                     PrintWriter out = new PrintWriter(
                         new BufferedWriter(new OutputStreamWriter(proc.getOutputStream())), true)) {
                    out.println("nmap --script=vuln ".concat(mecHost));
                    String line = "";
                    while ((line = in.readLine()) != null && Character.isDigit(line.trim().charAt(0))) {
                        //111/tcp status serivce
                        if (isSysPort(line)) {
                            continue;
                        }
                        //scan end.
                        if (line.startsWith("Nmap done")) {
                            break;
                        }
                        //contains vul scanning result
                        while ((line = in.readLine()) != null && line.trim().startsWith("|")) {
                            //contains vul scan result
                            if (line.contains(VULNERABLE)) {
                                return VULNERABLE_SCANNING_FAILED;
                            }
                        }
                    }
                } finally {
                    proc.destroy();
                }
            }
        } catch (Exception e) {
            return INNER_EXCEPTION;
        }

        return SUCCESS;
    }

    /**
     * system port, not app.
     *
     * @param line report result line
     * @return is system port
     */
    private boolean isSysPort(String line) {
        if (line.contains("dsp") || line.contains("rpcbind") || line.contains("nfs") || line.contains("http") || line
            .contains("https")) {
            return true;
        }
        return false;
    }

    /**
     * get app instantiate ip from context.
     *
     * @param context context info
     * @return instantiate mec host
     */
    private String getMecHostAppInstantiated(Map<String, String> context) {
        String mecHostIpList = context.get("mecHostIpList");
        if (null == mecHostIpList) {
            return null;
        }
        String[] hostArray = mecHostIpList.split(",");
        return hostArray[0];
    }

}
