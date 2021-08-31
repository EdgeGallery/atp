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
 * mec host netowrk delay validation.
 */
public class NetworkDelayValidation {

    private static final String NETWORK_DELAY_LONG = "network delay to %s is: %s, more than 50ms.";

    private static final String INNER_EXCEPTION = "inner exception, please check the log.";

    private static final String SUCCESS = "success";

    /**
     * execute test case.
     *
     * @param filePath csar file path
     * @param context context info
     * @return execute result
     */
    public String execute(String filePath, Map<String, String> context) throws Exception {
        String mecHost = getMecHostAppInstantiated(context);
        if (null == mecHost) {
            return SUCCESS;
        }
        try {
            Process proc = Runtime.getRuntime().exec("/bin/bash", null, new File("/bin"));
            if (null != proc) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                     PrintWriter out = new PrintWriter(
                         new BufferedWriter(new OutputStreamWriter(proc.getOutputStream())), true)) {
                    out.println("nping ".concat(mecHost));
                    String line = "";
                    double sendTime = 0;
                    double receiveTime = 0;
                    while ((line = in.readLine()) != null) {
                        if (line.startsWith("SENT")) {
                            //time end with s, example: 0.0013s
                            sendTime = Double.valueOf(line.substring(line.indexOf("(") + 1, line.indexOf(")") - 1));
                            continue;
                        }
                        if (line.startsWith("RCVD")) {
                            receiveTime = Double.valueOf(line.substring(line.indexOf("(") + 1, line.indexOf(")") - 1));
                            //network delay more than 50ms is a little slower
                            double delayTime = receiveTime - sendTime;
                            return delayTime > 0.05 ? String
                                .format(NETWORK_DELAY_LONG, mecHost, String.valueOf(delayTime)) : SUCCESS;
                        }
                    }
                } finally {
                    proc.destroy();
                }
            }
        } catch (Exception e) {
        }

        return INNER_EXCEPTION;
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
