package org.aniket.quick.mac.helper;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TimeHelper {

    public static String getTime() {
        final long ts = System.currentTimeMillis();
        final Instant instant = Instant.ofEpochMilli(ts);
        final ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return zonedDateTime.format(formatter);
    }

    public static String getTimeHeader() {
        return "Timestamp : " + getTime();
    }
}
