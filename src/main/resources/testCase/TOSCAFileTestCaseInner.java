
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

/**
 * Implementation of validating TOSCA.meta file.
 */
public class TOSCAFileTestCaseInner {

    private static final String TOSCA_META = "TOSCA.meta";

    private static final String INNER_EXCEPTION = "inner exception, please check the log.";

    private static final String TOSCA_FILE_NOT_EXISTS = "tosca.meta not exists.";

    private static final String TOSCA_LOSS_FIELD = "tosca.meta file may lost the following filed:Entry-Definitions.";

    private static final String FILE_NOT_EXIT = "the value of field Entry-Definitions do not exist corresponding file";

    private static Set<String> pathSet = new HashSet<String>();

    private static Set<String> field = new HashSet<String>() {
        {
            add("Entry-Definitions");
        }
    };

    public String execute(String filePath, Map<String, String> context) {
        try {
            Thread.sleep(1800);
        } catch (InterruptedException e1) {
        }
        Set<String> sourcePathSet = new HashSet<String>();
        boolean isExistTosca = false;
        try (ZipFile zipFile = new ZipFile(filePath)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String entryName = entry.getName();
                String path = entryName.substring(entryName.indexOf("/") + 1).trim();

                // suit for pattern of Artifacts/test,not Artifacts/test/
                pathSet.add(removeLastSlash(path));

                if (TOSCA_META.equals(entryName.substring(entryName.lastIndexOf("/") + 1).trim())) {
                    isExistTosca = true;
                    // some fields not exist in tosca.meta file
                    if (!isExistAll(zipFile, entry, field)) {
                        return TOSCA_LOSS_FIELD;
                    }
                    sourcePathSet = getPathSet(zipFile, entry, field);
                }
            }
        } catch (IOException e) {
            return INNER_EXCEPTION;
        }

        return isExistTosca == false
                ? TOSCA_FILE_NOT_EXISTS
                : pathSet.containsAll(sourcePathSet) ? "success" : FILE_NOT_EXIT;
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

    private String removeLastSlash(String path) {
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    private Set<String> getPathSet(ZipFile zipFile, ZipEntry entry, Set<String> prefixSet) {
        Set<String> pathSet = new HashSet<String>();
        try (BufferedReader br =
                new BufferedReader(new InputStreamReader(zipFile.getInputStream(entry), StandardCharsets.UTF_8))) {
            String line = "";
            while ((line = br.readLine()) != null) {
                String[] splitByColon = line.split(":");
                // prefix: path
                if (splitByColon.length > 1 && prefixSet.contains(splitByColon[0])) {
                    pathSet.add(splitByColon[1].trim());
                }
            }
        } catch (IOException e) {
        }

        return pathSet;
    }

}
