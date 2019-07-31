package com.calliduscloud.scas.scim_services.util;

import java.sql.Timestamp;
import java.util.Date;

public class CommonUtils {

    public static Timestamp getTimeStamp() {
        Date date = new Date();
        long time = date.getTime();
        Timestamp currentTime = new Timestamp(time);
        return currentTime;
    }
}
