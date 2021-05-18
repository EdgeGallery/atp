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

package org.edgegallery.atp.constant;

public interface ErrorCode {
    /**
     * retCode: success.
     */
    int RET_CODE_SUCCESS = 0;

    /**
     * retCode: fail.
     */
    int RET_CODE_FAILURE = 1;

    /**
     * retCode: partial success.
     */
    int RET_CODE_PARTIAL_SUCCESS = 5000;

    /**
     * DB error.
     */
    int DB_ERROR = 40100;

    String DB_ERROR_MSG = "DB error";

    /**
     * test scenario name in test suite not exists.
     */
    int TEST_SUITE_SCENARIO_NAME_NOT_EXISTS = 40110;

    String TEST_SUITE_SCENARIO_NAME_NOT_EXISTS_MSG = "test scenario name in test suite not exists.";

    /**
     * test suite name in test case not exists.
     */
    int TEST_CASE_TEST_SUITE_NAME_NOT_EXISTS = 40111;

    String TEST_CASE_TEST_SUITE_NAME_NOT_EXISTS_MSG = "test suite name in test case not exists.";

    /**
     * name already exists.
     */
    int NAME_EXISTS = 40112;

    String NAME_EXISTS_MSG = "name already exists";

    /**
     * file operation error.
     */
    int FILE_OPERATION_FAILED = 40113;

    String FILE_OPERATION_FAILED_MSG = "file operation error";

    /**
     * there does not exists test case script in test case file dir.
     */
    int TEST_CASE_NOT_EXISTS_IN_DIR = 40114;

    String TEST_CASE_NOT_EXISTS_IN_DIR_MSG = "there does not exists test case script in test case file dir";

    /**
     * length check failed.
     */
    int LENGTH_CHECK_FAILED = 40115;

    String LENGTH_CHECK_FAILED_MSG = "length check failed";
    
    /**
     * test case type error, must be: automatic or manual.
     */
    int TEST_CASE_TYPE_ERROR = 40116;
    
    String TEST_CASE_TYPE_ERROR_MSG = "test case type error, must be: automatic or manual";

    /**
     * test case language error, must be: java, python or jar.
     */
    int TEST_CASE_LANGUAGE_ERROR = 40117;

    String TEST_CASE_LANGUAGE_ERROR_MSG = "test case language error, must be: java, python or jar";

    /**
     * number of each sheet can not more than 1000.
     */
    int MAX_IMPORT_NUMBER_ERROR = 40118;

    String MAX_IMPORT_NUMBER_ERROR_MSG = "number of each sheet can not more than 1000";
}
