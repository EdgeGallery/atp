
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class YamlDescriptionFileValidation {
    private static final String YAML_FILE_NOT_EXISTS = "there is no yaml file in APPD/Definition/ dir.";
    private static final String INNER_EXCEPTION = "inner exception, please check the log.";
    private static final String VM = "vm";
    private static final String SUCCESS = "success";

    public String execute(String filePath, Map<String, String> context) {
        try {
            Thread.sleep(200);
            if (VM.equalsIgnoreCase(getAppType(filePath))) {
                return SUCCESS;
            }
        } catch (InterruptedException e1) {
        }
        try (ZipFile zipFile = new ZipFile(filePath)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                // APPD/Definition/MainServiceTemplate.yaml
                String[] nameArray = entry.getName().split("/");
                if (nameArray.length == 3 && "APPD".equalsIgnoreCase(nameArray[0])
                        && "Definition".equalsIgnoreCase(nameArray[1]) && nameArray[2].endsWith(".yaml")) {
                    return "success";
                }
            }
        } catch (IOException e) {
            return INNER_EXCEPTION;
        }

        return YAML_FILE_NOT_EXISTS;
    }

    /**
     * get app_type
     * 
     * @param filePath filePath
     * @return appType
     */
    private String getAppType(String filePath) {
        try (ZipFile zipFile = new ZipFile(filePath)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.getName().split("/").length == 1 && entry.getName().endsWith(".mf")) {
                    try (BufferedReader br = new BufferedReader(
                            new InputStreamReader(zipFile.getInputStream(entry), StandardCharsets.UTF_8))) {
                        String line = "";
                        while ((line = br.readLine()) != null) {
                            // prefix: path
                            if (line.trim().startsWith("app_class")) {
                                return line.split(":")[1].trim();
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
        }
        return null;
    }

}
