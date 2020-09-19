package org.edgegallery.atp.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonUtil {

    /*
     * get time according to special format
     */
    public static String getFormatDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(new Date());
    }

}
