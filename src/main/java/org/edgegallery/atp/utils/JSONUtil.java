package org.edgegallery.atp.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.edgegallery.atp.model.testcase.TestCaseDetail;
import org.edgegallery.atp.model.testcase.TestCaseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(JSONUtil.class);

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * transfer object to String.
     * 
     * @param obj obj
     * @return String type variable
     * @throws IOException
     */
    public static String marshal(Object obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            LOGGER.error("marshal obj failed. {}", obj);
            throw new IllegalArgumentException("marshal obj failed.");
        }
    }

    /**
     * transfer String type to special model type.
     * 
     * @param <T>
     * @param src souce String
     * @param type target type
     * @return target type model
     * @throws IOException
     */
    public static <T> T unMarshal(String src, Class<T> type) {
        try {
            return MAPPER.readValue(src, type);
        } catch (IOException e) {
            LOGGER.error("unmarshal obj failed. {}", src);
            throw new IllegalArgumentException("unmarshal obj failed.");
        }
    }

    public static void main(String[] args) {
        TestCaseDetail testcaseDetail = new TestCaseDetail();
        List<Map<String, TestCaseResult>> virusScanningTest = new ArrayList<Map<String, TestCaseResult>>();
        List<Map<String, TestCaseResult>> complianceTest = new ArrayList<Map<String, TestCaseResult>>();
        List<Map<String, TestCaseResult>> sandboxTest = new ArrayList<Map<String, TestCaseResult>>();
        TestCaseResult result = new TestCaseResult();
        Map<String, TestCaseResult> map = new HashMap<String, TestCaseResult>();
        map.put("a", result);
        map.put("b", result);
        virusScanningTest.add(map);
        complianceTest.add(map);
        sandboxTest.add(map);
        testcaseDetail.setComplianceTest(complianceTest);
        testcaseDetail.setVirusScanningTest(virusScanningTest);
        testcaseDetail.setSandboxTest(sandboxTest);


        String a = JSONUtil.marshal(testcaseDetail);
        a = a.replaceAll("\\[|\\]", "");

        Map<String, Object> result111 = new HashMap<String, Object>();
        result111 = JSONUtil.unMarshal(a, Map.class);
        Map<String, Object> result222 = new HashMap<String, Object>();
        result222 = JSONUtil.unMarshal(a, Map.class);

        Map<String, Object> finala = new HashMap<String, Object>();
        finala.put("task1", result111);
        finala.put("task2", result222);
        Yaml yaml = new Yaml();
        String str = yaml.dump(finala);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(str);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        byte[] bytes = byteArrayOutputStream.toByteArray();

        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        try {
            ObjectInputStream ois = new ObjectInputStream(bais);
            try {
                System.out.print(ois.readObject());
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }
}
