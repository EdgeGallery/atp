
import java.util.Map;

/**
 * execute virus scan class.
 *
 */
public class VirusScanTestCaseInner {

    public String execute(String filePath, Map<String, String> context) {
        try {
            Thread.sleep(1300);
        } catch (InterruptedException e1) {
        }
        return "success";
    }

}