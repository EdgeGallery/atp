
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of validating .mf file must be in root directory.
 */
public class SuffixTestCaseInner {

    private static final Logger LOGGER = LoggerFactory.getLogger(SuffixTestCaseInner.class);

    private static final String INNER_EXCEPTION = "inner exception, please check the log.";

    private static final String FILE_NOT_EXIST = ".mf file may not exist or it do not in the root directory.";

    public String execute(String filePath, Map<String, String> context) {
        try (ZipFile zipFile = new ZipFile(filePath)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                // root directory and file is end of mf
                if (entry.getName().split("/").length == 2 && fileSuffixValidate("mf", entry.getName())) {
                    return "success";
                }
            }
        } catch (IOException e) {
            LOGGER.error("SuffixTestCase execute failed. {}", e.getMessage());
            return INNER_EXCEPTION;
        }
        return FILE_NOT_EXIST;
    }

    public static boolean fileSuffixValidate(String pattern, String fileName) {
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        if (StringUtils.isNotBlank(suffix) && suffix.equals(pattern)) {
            return true;
        }
        return false;
    }

}
