
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class VirtualMemoryDescriptionValidation {
    private static final String VIRTUAL_MEMORY_NOT_EXISTS = "There is no virtual memory description filed: virtual_mem_size";
    private static final String INNER_EXCEPTION = "inner exception, please check the log.";
    private static final String VIRTUAL_MEM_SIZE = "virtual_mem_size";

    private static final String VM = "vm";

    private static final String SUCCESS = "success";

    public String execute(String filePath, Map<String, String> context) {
        try {
            Thread.sleep(1000);
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
                    return hasVirtualMemDescription(zipFile, entry) ? SUCCESS : VIRTUAL_MEMORY_NOT_EXISTS;
                }
            }
        } catch (IOException e) {
            return INNER_EXCEPTION;
        }
        
        return INNER_EXCEPTION;
    }

    private static boolean hasVirtualMemDescription(ZipFile zipFile, ZipEntry entry) {
        try (BufferedReader br =
                new BufferedReader(new InputStreamReader(zipFile.getInputStream(entry), StandardCharsets.UTF_8))) {
            String line = "";
            while ((line = br.readLine()) != null) {
                if(line.trim().startsWith(VIRTUAL_MEM_SIZE)) {
                    String[] splitByColon = line.split(":");
                    // Source: path
                    if (splitByColon.length > 1 && VIRTUAL_MEM_SIZE.equals((splitByColon[0].trim()))) {
                          return true;   
                    }
                }
            }
        } catch (IOException e) {
        }

        return false;
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
