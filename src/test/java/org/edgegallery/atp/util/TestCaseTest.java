/**
 * Copyright 2020 Huawei Technologies Co.,Ltd.**Licensed under the Apache License,Version
 * 2.0(the"License");you may not use this file/except*in compliance with the License.You may obtain
 * a copy of the License at**http:// www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing,software distributed under the/License*is distributed on
 * an"AS IS"BASIS,WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,either/express*or implied.See the
 * License for the specific language governing permissions and limitations/under*the License.
 */

package org.edgegallery.atp.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.ibatis.io.Resources;
import org.edgegallery.atp.ATPApplicationTest;
import org.edgegallery.atp.schedule.testcase.TestCaseHandler;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ATPApplicationTest.class)
@AutoConfigureMockMvc
public class TestCaseTest {

    @Test
    public void suffixTestCaseTestNormal() throws IOException {
        String pkgPth = "org.edgegallery.atp.schedule.testcase.compliance.SuffixTestCase";
        String filePath = "testfile/AR.csar";
        Assert.assertTrue(getTestCaseHandler(pkgPth, filePath));
    }

    @Test
    public void suffixTestCaseTestAbNormal() throws IOException {
        String pkgPth = "org.edgegallery.atp.schedule.testcase.compliance.SuffixTestCase";
        String filePath = "testfile/ARNoMfTosca.csar";
        Assert.assertFalse(getTestCaseHandler(pkgPth, filePath));
    }

    @Test
    public void sourcePathTestCaseNormal() throws IOException {
        String pkgPth = "org.edgegallery.atp.schedule.testcase.compliance.SourcePathTestCase";
        String filePath = "testfile/AR.csar";
        Assert.assertTrue(getTestCaseHandler(pkgPth, filePath));
    }

    @Test
    public void sourcePathTestCaseAbNormal() throws IOException {
        String pkgPth = "org.edgegallery.atp.schedule.testcase.compliance.SourcePathTestCase";
        String filePath = "testfile/ARException.csar";
        Assert.assertFalse(getTestCaseHandler(pkgPth, filePath));
    }

    @Test
    public void mfContentTestCaseNormal() throws IOException {
        String pkgPth = "org.edgegallery.atp.schedule.testcase.compliance.MFContentTestCase";
        String filePath = "testfile/AR.csar";
        Assert.assertTrue(getTestCaseHandler(pkgPth, filePath));
    }

    @Test
    public void mfContentTestCaseAbNormal() throws IOException {
        String pkgPth = "org.edgegallery.atp.schedule.testcase.compliance.MFContentTestCase";
        String filePath = "testfile/ARException.csar";
        Assert.assertFalse(getTestCaseHandler(pkgPth, filePath));
    }

    @Test
    public void toscaFileTestCaseNormal() throws IOException {
        String pkgPth = "org.edgegallery.atp.schedule.testcase.compliance.TOSCAFileTestCase";
        String filePath = "testfile/AR.csar";
        Assert.assertTrue(getTestCaseHandler(pkgPth, filePath));
    }

    @Test
    public void toscaFileTestCaseWithoutTosca() throws IOException {
        String pkgPth = "org.edgegallery.atp.schedule.testcase.compliance.TOSCAFileTestCase";
        String filePath = "testfile/ARNoMfTosca.csar";
        Assert.assertFalse(getTestCaseHandler(pkgPth, filePath));
    }

    @Test
    public void toscaFileTestCaseFieldException() throws IOException {
        String pkgPth = "org.edgegallery.atp.schedule.testcase.compliance.TOSCAFileTestCase";
        String filePath = "testfile/ARException.csar";
        Assert.assertFalse(getTestCaseHandler(pkgPth, filePath));
    }

    private boolean getTestCaseHandler(String pkgPth, String filePath) throws IOException {
        TestCaseHandler testCaseHandler = new TestCaseHandler();
        File csarFile = Resources.getResourceAsFile(filePath);
        String filePathCanonical = csarFile.getCanonicalPath();
        Map<String, String> context = new HashMap<String, String>();
        return "success".equals(testCaseHandler.testCaseHandler(pkgPth, filePathCanonical, context).getResult()) ? true
                : false;
    }
}
