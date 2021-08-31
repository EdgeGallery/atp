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
 * mec host available validation
 */
public class MecHostAccessValidation {

    private static final String MEC_HOST_NOT_ACCESS = "mec host ip: %s can not be accessed";

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
        String[] mecHostArray = getMecHostAppInstantiated(context);
        if (0 == mecHostArray.length) {
            return SUCCESS;
        }
        try {
            Process proc = Runtime.getRuntime().exec("/bin/bash", null, new File("/bin"));
            if (null != proc) {
                for (String mecHost : mecHostArray) {
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                         PrintWriter out = new PrintWriter(
                             new BufferedWriter(new OutputStreamWriter(proc.getOutputStream())), true)) {
                        out.println("nping ".concat(mecHost));
                        String line = "";
                        while ((line = in.readLine()) != null) {
                            if (line.startsWith("TCP connection")) {
                                String[] field = line.split("\\|");
                                if (field.length > 1) {
                                    String result = field[field.length - 1].trim();
                                    String[] failedResult = result.split(":");
                                    if (failedResult.length > 1 && failedResult[1].trim().startsWith("0")) {
                                        break;
                                    } else {
                                        return String.format(MEC_HOST_NOT_ACCESS, mecHost);
                                    }
                                }
                            }
                        }
                    } finally {
                        proc.destroy();
                    }
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
    private String[] getMecHostAppInstantiated(Map<String, String> context) {
        String mecHostIpList = context.get("mecHostIpList");
        if (null == mecHostIpList) {
            return null;
        }
        String[] hostArray = mecHostIpList.split(",");
        return hostArray;
    }

}
