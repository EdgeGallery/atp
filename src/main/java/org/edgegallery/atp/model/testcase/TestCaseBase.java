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

package org.edgegallery.atp.model.testcase;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import org.edgegallery.atp.model.Entity;

@Setter
@Getter
public class TestCaseBase implements Entity {

    /**
     * test case id.
     */
    private String id;

    /**
     * chinese test case name.
     */
    private String nameCh;

    /**
     * english test case name.
     */
    private String nameEn;

    /**
     * test case type, antomatic or manual.
     */
    private String type;

    /**
     * package path of test case class.
     */
    private String className;

    /**
     * chinese description of test case.
     */
    private String descriptionCh;

    /**
     * english description of test case.
     */
    private String descriptionEn;

    /**
     * content of test case souce code. The reserved field.
     */
    private String hashCode;

    /**
     * file storage path.
     */
    private String filePath;

    /**
     * test case language.
     */
    private String codeLanguage;

    /**
     * expect test result in chinese.
     */
    private String expectResultCh;

    /**
     * expect test result in english.
     */
    private String expectResultEn;

    /**
     * test step in chinese.
     */
    private String testStepCh;

    /**
     * test step in english.
     */
    private String testStepEn;

    /**
     * create time.
     */
    private Date createTime;

    /**
     * set create time.
     * 
     * @param createTime createTime
     */
    public void setCreateTime(Date createTime) {
        if (createTime != null) {
            this.createTime = (Date) createTime.clone();
        } else {
            this.createTime = null;
        }
    }

    /**
     * get create time.
     * 
     * @return date
     */
    public Date getCreateTime() {
        return createTime != null ? (Date) createTime.clone() : null;
    }

    public TestCaseBase() {

    }

    /**
     * construct function.
     * 
     * @param builder builder
     */
    public TestCaseBase(Builder builder) {
        this.id = builder.id;
        this.nameCh = builder.nameCh;
        this.nameEn = builder.nameEn;
        this.descriptionCh = builder.descriptionCh;
        this.descriptionEn = builder.descriptionEn;
        this.className = builder.className;
        this.codeLanguage = builder.codeLanguage;
        this.expectResultCh = builder.expectResultCh;
        this.expectResultEn = builder.expectResultEn;
        this.filePath = builder.filePath;
        this.hashCode = builder.hashCode;
        this.testStepCh = builder.testStepCh;
        this.testStepEn = builder.testStepEn;
        this.type = builder.type;
    }

    /**
     * model change to test case.
     * 
     * @return test case info
     */
    public TestCase toTestCase() {
        TestCase testCase = new TestCase();
        testCase.setClassName(this.className);
        testCase.setCodeLanguage(this.codeLanguage);
        testCase.setDescriptionCh(this.descriptionCh);
        testCase.setDescriptionEn(this.descriptionEn);
        testCase.setExpectResultCh(this.expectResultCh);
        testCase.setExpectResultEn(this.expectResultEn);
        testCase.setFilePath(this.filePath);
        testCase.setHashCode(this.hashCode);
        testCase.setId(this.id);
        testCase.setNameCh(this.nameCh);
        testCase.setNameEn(this.nameEn);
        testCase.setTestStepCh(this.testStepCh);
        testCase.setTestStepEn(this.testStepEn);
        testCase.setType(this.type);
        testCase.setCreateTime(this.createTime);
        return testCase;
    }

    /**
     * model change to test case po.
     * 
     * @return test case po
     */
    public TestCasePo toTestCasePo() {
        TestCasePo testCasePo = new TestCasePo();
        testCasePo.setClassName(this.className);
        testCasePo.setCodeLanguage(this.codeLanguage);
        testCasePo.setDescriptionCh(this.descriptionCh);
        testCasePo.setDescriptionEn(this.descriptionEn);
        testCasePo.setExpectResultCh(this.expectResultCh);
        testCasePo.setExpectResultEn(this.expectResultEn);
        testCasePo.setFilePath(this.filePath);
        testCasePo.setHashCode(this.hashCode);
        testCasePo.setId(this.id);
        testCasePo.setNameCh(this.nameCh);
        testCasePo.setNameEn(this.nameEn);
        testCasePo.setTestStepCh(this.testStepCh);
        testCasePo.setTestStepEn(this.testStepEn);
        testCasePo.setType(this.type);
        testCasePo.setCreateTime(this.createTime);
        return testCasePo;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String id;

        private String nameCh;

        private String nameEn;

        private String type;

        private String className;

        private String descriptionCh;

        private String descriptionEn;

        private String hashCode;

        private String filePath;

        private String codeLanguage;

        private String expectResultCh;

        private String expectResultEn;

        private String testStepCh;

        private String testStepEn;

        private Builder() {

        }

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setNameEn(String nameEn) {
            this.nameEn = nameEn;
            return this;
        }

        public Builder setNameCh(String nameCh) {
            this.nameCh = nameCh;
            return this;
        }

        public Builder setDescriptionEn(String descriptionEn) {
            this.descriptionEn = descriptionEn;
            return this;
        }

        public Builder setDescriptionCh(String descriptionCh) {
            this.descriptionCh = descriptionCh;
            return this;
        }

        public Builder setHashCode(String hashCode) {
            this.hashCode = hashCode;
            return this;
        }

        public Builder setType(String type) {
            this.type = type;
            return this;
        }

        public Builder setClassName(String className) {
            this.className = className;
            return this;
        }

        public Builder setFilePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public Builder setCodeLanguage(String codeLanguage) {
            this.codeLanguage = codeLanguage;
            return this;
        }

        public Builder setExpectResultCh(String expectResultCh) {
            this.expectResultCh = expectResultCh;
            return this;
        }

        public Builder setExpectResultEn(String expectResultEn) {
            this.expectResultEn = expectResultEn;
            return this;
        }

        public Builder setTestStepCh(String testStepCh) {
            this.testStepCh = testStepCh;
            return this;
        }

        public Builder setTestStepEn(String testStepEn) {
            this.testStepEn = testStepEn;
            return this;
        }

        public TestCaseBase build() {
            return new TestCaseBase(this);

        }
    }
}
