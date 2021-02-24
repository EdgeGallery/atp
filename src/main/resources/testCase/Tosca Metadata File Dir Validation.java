
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ToscaMetadataValidation {
    private static final String TOSCA_METADATA_NOT_EXISTS = "root path must contain TOSCA-Metadata file dir.";
    private static final String INNER_EXCEPTION = "inner exception, please check the log.";

    public String execute(String filePath, Map<String, String> context) {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e1) {
        }
        try (ZipFile zipFile = new ZipFile(filePath)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.getName().startsWith("TOSCA-Metadata/")) {
                    return "success";
                }
            }
        } catch (IOException e) {
            return INNER_EXCEPTION;
        }

        return TOSCA_METADATA_NOT_EXISTS;
    }
}
