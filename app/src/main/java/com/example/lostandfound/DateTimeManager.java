package com.example.lostandfound;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class DateTimeManager {

    private LocalDateTime localDateTime;

    // Use the time provided with 5 integers
    public DateTimeManager(int year, int month, int day, int hour, int minute){
        this.localDateTime = LocalDateTime.of(year, month, day, hour, minute);
    }

    // Use the epoch time (In seconds)
    public DateTimeManager(long epoch){
        this.localDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(epoch), ZoneId.systemDefault());
    }

    // Use the current time
    public DateTimeManager(){
        this.localDateTime = LocalDateTime.now();
    }

    // output epoch time in seconds
    public long getEpoch(){
        return 0L;
    }

    // get the time in a formatted string
    public String getFormattedString(){
        return "";
    }

    // get the integer fields
    public int getYear(){
        return 0;
    }

    public int getMonth(){
        return 0;
    }

    public int getDay(){
        return 0;
    }

    public int getHour(){
        return 0;
    }

    public int getMinute(){
        return 0;
    }

}
