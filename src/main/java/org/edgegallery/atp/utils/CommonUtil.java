package org.edgegallery.atp.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import org.edgegallery.atp.constant.Constant;
import org.edgegallery.atp.utils.file.FileChecker;
import org.springframework.web.multipart.MultipartFile;

public class CommonUtil {

    /**
     * get time according to special format
     * 
     * @return time
     */
    public static String getFormatDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(new Date());
    }

    /**
     * generate uuid randomly
     * 
     * @return uuid
     */
    public static String generateId() {
        return UUID.randomUUID().toString();
    }

    /**
     * delete temp file according to fileId and file.
     * 
     * @param fileId taskId
     * @param file csar file
     */
    public static void deleteTempFile(String fileId, MultipartFile file) {
        new File(new StringBuilder().append(FileChecker.getDir()).append(File.separator).append("temp")
                .append(File.separator).append(fileId).append(Constant.UNDER_LINE).append(file.getOriginalFilename())
                .toString()).delete();
    }
}
