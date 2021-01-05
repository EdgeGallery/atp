
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

public class SourcePathTestCaseInner {

    private static final String SLASH = "/";

    private static final String INNER_EXCEPTION = "inner exception, please check the log.";

    private static final String SOURCE_PATH_FILE_NOT_EXISTS = "some source path file in .mf may not exist.";

    private static Set<String> pathSet = new HashSet<String>();

    public String execute(String filePath, Map<String, String> context) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e1) {
        }
        Set<String> sourcePathSet = new HashSet<String>();
        try (ZipFile zipFile = new ZipFile(filePath)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String entryName = entry.getName();

                String path = entryName.substring(entryName.indexOf(SLASH) + 1).trim();
                pathSet.add(removeLastSlash(path));

                // root directory and file is end of mf
                if (entry.getName().split(SLASH).length == 2
                        && fileSuffixValidate("mf", entry.getName())) {
                    Set<String> prefix = new HashSet<String>() {
                        {
                            add("Source");
                        }
                    };
                    sourcePathSet = getPathSet(zipFile, entry, prefix);
                }
            }
        } catch (IOException e) {
            return INNER_EXCEPTION;
        }
        return pathSet.containsAll(sourcePathSet) ? "success" : SOURCE_PATH_FILE_NOT_EXISTS;
    }

    private boolean fileSuffixValidate(String pattern, String fileName) {
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        if ((null != suffix) && suffix.equals(pattern)) {
            return true;
        }
        return false;
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

    private String removeLastSlash(String path) {
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }
}
