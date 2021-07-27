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
     * test scenario name in test suite not exists.
     */
    int TEST_SUITE_SCENARIO_NAME_NOT_EXISTS = 40000;

    String TEST_SUITE_SCENARIO_NAME_NOT_EXISTS_MSG = "test scenario name in test suite not exists.";

    /**
     * test suite name in test case not exists.
     */
    int TEST_CASE_TEST_SUITE_NAME_NOT_EXISTS = 40001;

    String TEST_CASE_TEST_SUITE_NAME_NOT_EXISTS_MSG = "test suite name in test case not exists.";

    /**
     * name already exists.
     */
    int NAME_EXISTS = 40002;

    String NAME_EXISTS_MSG = "name %s already exists.";

    /**
     * DB error.
     */
    int DB_ERROR = 40003;

    String DB_ERROR_MSG = "DB error: %s.";

    /**
     * there does not exists test case script in test case file dir.
     */
    int TEST_CASE_NOT_EXISTS_IN_DIR = 40004;

    String TEST_CASE_NOT_EXISTS_IN_DIR_MSG = "there does not exists test case script in test case file dir.";

    /**
     * length check failed.
     */
    int LENGTH_CHECK_FAILED = 40005;

    String LENGTH_CHECK_FAILED_MSG = "length check failed.";

    /**
     * test case type error, must be: automatic or manual.
     */
    int TEST_CASE_TYPE_ERROR = 40006;

    String TEST_CASE_TYPE_ERROR_MSG = "test case type error, must be: automatic or manual.";

    /**
     * test case language error, must be: java, python or jar.
     */
    int TEST_CASE_LANGUAGE_ERROR = 40007;

    String TEST_CASE_LANGUAGE_ERROR_MSG = "test case language error, must be: java, python or jar.";

    /**
     * number of each sheet can not more than 1000.
     */
    int MAX_IMPORT_NUMBER_ERROR = 40008;

    String MAX_IMPORT_NUMBER_ERROR_MSG = "number of each sheet can not more than 1000.";

    /**
     * param is null.
     */
    int PARAM_IS_NULL = 40009;

    String PARAM_IS_NULL_MSG = "%s is null.";

    /**
     * file name contains blank.
     */
    int FILE_NAME_CONTAIN_BLANK = 40010;

    String FILE_NAME_CONTAIN_BLANK_MSG = "fileName contain blank.";

    /**
     * file name illegal.
     */
    int FILE_NAME_ILLEGAL = 40011;

    String FILE_NAME_ILLEGAL_MSG = "fileName illegal.";

    /**
     * file operation io exception.
     */
    int FILE_IO_EXCEPTION = 40012;

    String FILE_IO_EXCEPTION_MSG = "file operation io exception.";

    /**
     * not exists in system.
     */
    int NOT_FOUND_EXCEPTION = 40013;

    String NOT_FOUND_EXCEPTION_MSG = "%s does not exist.";

    /**
     * param1 size is out of limit param2.
     */
    int SIZE_OUT_OF_LIMIT = 40014;

    String SIZE_OUT_OF_LIMIT_MSG = "%s size out of limit %s.";

    /**
     * param1 number is out of limit param2.
     */
    int NUMBER_OUT_OF_LIMIT = 40015;

    String NUMBER_OUT_OF_LIMIT_MSG = "%s number out of limit %s.";

    /**
     * task is alreay running.
     */
    int TASK_IS_RUNNING = 40016;

    String TASK_IS_RUNNING_MSG = "task is alreay running.";

    /**
     * run task failed.
     */
    int RUN_TASK_FAILED = 40017;

    String RUN_TASK_FAILED_MSG = "run task failed.";

    /**
     * bomb defense failed.
     */
    int BOMB_DEFENSE_FAILED = 40018;

    String BOMB_DEFENSE_FAILED_MSG = "bomb defense failed.";

    /**
     * file pattern not right.
     */
    int PATTERN_CHECK_FAILED = 40019;

    String PATTERN_CHECK_FAILED_MSG = "file pattern is not right, must be: %s";

    /**
     * the scenario is used by some test suites, so can not be delete.
     */
    int TEST_SCENARIO_IS_CITED = 40020;

    String TEST_SCENARIO_IS_CITED_MSG = "the scenario is used by some test suites, so can not be delete.";

    /**
     * the test suite is used by some test cases, can not be deleted.
     */
    int TEST_SUITE_IS_CITED = 40021;

    String TEST_SUITE_IS_CITED_MSG = "the test suite is used by some test cases, can not be deleted.";

    /**
     * test case type in testSuiteIds is not the same as others, must be all automatic or all manual.
     */
    int TEST_CASE_TYPE_COMPATIBILITY_ERROR = 40022;

    String TEST_CASE_TYPE_COMPATIBILITY_ERROR_MSG = "test case type in testSuiteIds is not the same as others.";

    /**
     * config param pattern error.
     */
    int CONFIG_PARAM_PATTERN_ERROR = 40023;

    String CONFIG_PARAM_PATTERN_ERROR_MSG
        = "config param pattern wrong,each param must split by ; and key value must split by =";
}
