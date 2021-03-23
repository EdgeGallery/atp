package org.edgegallery.atp.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;
import org.apache.commons.io.FileUtils;
import org.apache.http.entity.ContentType;
import org.apache.ibatis.io.Resources;
import org.edgegallery.atp.ATPApplicationTest;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.interfaces.filter.AccessTokenFilter;
import org.edgegallery.atp.utils.CommonUtil;
import org.edgegallery.atp.utils.FileChecker;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import mockit.Mock;
import mockit.MockUp;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ATPApplicationTest.class)
@AutoConfigureMockMvc
public class CommonUtilTest {
    private static final String BASIC_PATH = FileChecker.getDir() + File.separator + "testCase" + File.separator;

    @Before
    public void setUp() throws Exception {
        Map<String, String> contextMap = new HashMap<String, String>();
        contextMap.put(Constant.ACCESS_TOKEN, "aaa");
        contextMap.put(Constant.USER_ID, "4eed814b-5d29-4e4c-ba73-51fc6db4ed86");
        contextMap.put(Constant.USER_NAME, "atp");
        AccessTokenFilter.context.set(contextMap);
    }

    @Test
    public void testCommonUtil() throws IOException {
        CommonUtil.getFormatDate();
        
        // deleteTempFile
        File csar = Resources.getResourceAsFile("testfile/AR.csar");
        InputStream csarInputStream = new FileInputStream(csar);
        MultipartFile csarMultiFile = new MockMultipartFile("AR.csar", "AR.csar",
                ContentType.APPLICATION_OCTET_STREAM.toString(), csarInputStream);
        CommonUtil.deleteTempFile(UUID.randomUUID().toString(), csarMultiFile);

        // get app info from appstore
        new MockUp<RestTemplate>() {
            @Mock
            public <T> ResponseEntity<T> exchange(String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity,
                    Class<T> responseType, Object... uriVariables) throws RestClientException {
                ResponseEntity<String> response = new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
                return (ResponseEntity<T>) response;
            }
        };
        CommonUtil.getAppInfoFromAppStore("111", "111");

        // downloadAppFromAppStore
        Map<String, String> context = new HashMap<String, String>();
        context.put(Constant.ACCESS_TOKEN, "token");
        CommonUtil.downloadAppFromAppStore("111", "111", context);
        context.put(Constant.TENANT_ID,"tenant");
        
        Map<String, String> appInfo = new HashMap<String, String>();
        appInfo.put(Constant.APP_NAME, "name");
        appInfo.put(Constant.PACKAGE_ID, "111");
        appInfo.put(Constant.APP_ID, "111");
        // deleteAppInstance
        CommonUtil.deleteAppInstance("111", context);

        // UUID exception
        try {
            CommonUtil.isUuidPattern("111");
        } catch (IllegalArgumentException e) {

        }

        // dependencyCheck exception
        File csarFile = Resources.getResourceAsFile("testfile/AR.csar");
        String filePath = BASIC_PATH + "AR";
        FileChecker.createFile(filePath);
        File targetFile = new File(filePath);
        FileCopyUtils.copy(csarFile, targetFile);
        Stack<Map<String, String>> dependencyStack = new Stack<Map<String, String>>();
        new MockUp<FileUtils>() {
            @Mock
            public void copyInputStreamToFile(final InputStream source, final File destination)
                    throws IOException {
                throw new IOException();
            }
        };
        try {
            CommonUtil.dependencyCheckSchdule(filePath, dependencyStack, context);
        } catch (IllegalArgumentException e) {

        }
    }
}
