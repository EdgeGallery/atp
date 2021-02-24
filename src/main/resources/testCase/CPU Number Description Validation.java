import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class CPUNumberDescriptionValidation {
    private static final String CPU_DESCRIPTION_NOT_EXISTS = "There is no cpu description filed: num_virtual_cpu";
    private static final String INNER_EXCEPTION = "inner exception, please check the log.";
    private static final String NUM_VIRTUAL_CPU = "num_virtual_cpu";

    public String execute(String filePath, Map<String, String> context) {
        try {
            Thread.sleep(200);
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
                    return hasCPUDescription(zipFile, entry) ? "success" : CPU_DESCRIPTION_NOT_EXISTS;
                }
            }
        } catch (IOException e) {
            return INNER_EXCEPTION;
        }
        
        return INNER_EXCEPTION;
    }

    private boolean hasCPUDescription(ZipFile zipFile, ZipEntry entry) {
        try (BufferedReader br =
                new BufferedReader(new InputStreamReader(zipFile.getInputStream(entry), StandardCharsets.UTF_8))) {
            String line = "";
            while ((line = br.readLine()) != null) {
                if(line.trim().startsWith(NUM_VIRTUAL_CPU)) {
                    String[] splitByColon = line.split(":");
                    // Source: path
                    if (splitByColon.length > 1 && NUM_VIRTUAL_CPU.equals((splitByColon[0].trim()))) {
                          return true;   
                    }
                }
            }
        } catch (IOException e) {
        }

        return false;
    }

}
