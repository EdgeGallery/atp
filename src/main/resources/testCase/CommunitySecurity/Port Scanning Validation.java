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
 * mec host scanning which app instantiated.
 */
public class PortScanningValidation {

    private static final String PORT_NOT_SURE = "there are some ports status is not sure,open|filtered: %s ";

    private static final String PORT_UNFILTERED = "there are some ports status is not sure,unfiltered: %s ";

    private static final String MEC_HOST_IS_EMPTY = "app instantiate ip not found. ";

    private static final String INNER_EXCEPTION = "inner exception, please check the log.";

    private static final String PORT_STATUS_UNFILTERED = "unfiltered";

    private static final String PORT_STATUS_NOT_SURE = "open|filtered";

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
                    out.println("nmap ".concat(mecHost));
                    String line = "";
                    boolean flag = false;
                    while ((line = in.readLine()) != null) {
                        if (line.trim().startsWith("PORT")) {
                            flag = true;
                            continue;
                        }
                        if (line.startsWith("Nmap done")) {
                            break;
                        }
                        if (flag && line.contains(PORT_STATUS_NOT_SURE)) {
                            return String.format(PORT_NOT_SURE, line);
                        }
                        if (flag && line.contains(PORT_STATUS_UNFILTERED)) {
                            return String.format(PORT_UNFILTERED, line);
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
