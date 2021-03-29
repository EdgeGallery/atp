
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class APPDValidation {
    private static final String APPD_NOT_EXISTS = "root path must contain APPD file dir.";
    private static final String INNER_EXCEPTION = "inner exception, please check the log.";

    public String execute(String filePath, Map<String, String> context) {
        try {
            Thread.sleep(800);
        } catch (InterruptedException e1) {
        }
        try (ZipFile zipFile = new ZipFile(filePath)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.getName().startsWith("APPD/")) {
                    return "success";
                }
            }
        } catch (IOException e) {
            return INNER_EXCEPTION;
        }

        return APPD_NOT_EXISTS;
    }
}
