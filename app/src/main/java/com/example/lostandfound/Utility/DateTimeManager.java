package com.example.lostandfound.Utility;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public final class DateTimeManager {

    private DateTimeManager(){}

    // convert to epoch time
    public static long toEpoch(int year, int month, int day, int hour, int minute){
        LocalDateTime givenDate = LocalDateTime.of(year, month, day, hour, minute);
        return givenDate.toEpochSecond(ZoneOffset.UTC);
    }

    // get the epoch time in a formatted string
    public static String getFormattedDate(long epoch){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = new Date(epoch*1000);   // convert to millis
        return dateFormat.format(date);
    }

}
