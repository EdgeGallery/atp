import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ArtifactsValidation {
    private static final String ARTIFACTS_NOT_EXISTS = "root path must contain Artifacts file dir.";
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
                if (entry.getName().startsWith("Artifacts/")) {
                    return "success";
                }
            }
        } catch (IOException e) {
            return INNER_EXCEPTION;
        }

        return ARTIFACTS_NOT_EXISTS;
    }
}
