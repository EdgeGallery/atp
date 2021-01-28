import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class MFContentTestCaseInner {

    private static final String MF_LOSS_FIELD =
            ".mf file may lost the following fileds:app_product_name,app_provider_id,app_package_version,app_release_date_time or app_package_description.";

    private static final String INNER_EXCEPTION = "inner exception, please check the log.";

    private static final String FILE_NOT_EXIST = ".mf file may not exist or it do not in the root directory.";

    private static Set<String> field = new HashSet<String>() {
        {
            add("app_product_name");
            add("app_provider_id");
            add("app_package_version");
            add("app_release_data_time");
            add("app_package_description");
            add("app_type");
        }
    };

    public String execute(String filePath, Map<String, String> context) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e1) {
        }
        try (ZipFile zipFile = new ZipFile(filePath)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.getName().split("/").length == 1 && fileSuffixValidate("mf", entry.getName())) {
                    // some fields not exist in tosca.meta file
                    return isExistAll(zipFile, entry, field)
                            ? "success"
                            : MF_LOSS_FIELD;
                }
            }
        } catch (IOException e) {
            return INNER_EXCEPTION;
        }

        return FILE_NOT_EXIST;
    }

    private boolean fileSuffixValidate(String pattern, String fileName) {
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        if ((null != suffix) && suffix.equals(pattern)) {
            return true;
        }
        return false;
    }

    private boolean isExistAll(ZipFile zipFile, ZipEntry entry, Set<String> prefixSet) {
        Set<String> sourcePathSet = new HashSet<String>();
        try (BufferedReader br =
                new BufferedReader(new InputStreamReader(zipFile.getInputStream(entry), StandardCharsets.UTF_8))) {
            String line = "";
            while ((line = br.readLine()) != null) {
                // prefix: path
                String[] splitByColon = line.split(":");
                if (splitByColon.length > 1 && prefixSet.contains(splitByColon[0].trim())) {
                    sourcePathSet.add(splitByColon[0].trim());
                }
            }
        } catch (IOException e) {
        }

        return sourcePathSet.containsAll(prefixSet);
    }
}
