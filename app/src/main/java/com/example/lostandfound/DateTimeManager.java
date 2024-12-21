package com.example.lostandfound;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeManager {

    private LocalDateTime localDateTime;

    // Use the time provided with 5 integers
    public DateTimeManager(int year, int month, int day, int hour, int minute){
        this.localDateTime = LocalDateTime.of(year, month, day, hour, minute);
    }

    // Use the epoch time (In seconds)
    public DateTimeManager(long epoch){
        this.localDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(epoch), ZoneId.of("UTC"));   // UTC for no time offsets
    }

    // Use the current time
    public DateTimeManager(){
        this.localDateTime = LocalDateTime.now();
    }

    // output epoch time in seconds
    public long getEpoch(){
        ZonedDateTime zonedDateTime = this.localDateTime.atZone(ZoneId.of("UTC"));
        return zonedDateTime.toEpochSecond();
    }

    // get the time in a formatted string
    // extract only the date part
    public String getFormattedDate(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        return formatter.format(this.localDateTime);
    }

    // extract only the time part in 24h format
    public String getFormattedTime(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return formatter.format(this.localDateTime);
    }

    // get the integer fields
    public int getYear(){
        return this.localDateTime.getYear();
    }

    public int getMonth(){
        return this.localDateTime.getMonthValue();
    }

    public int getDay(){
        return this.localDateTime.getDayOfMonth();
    }

    // return hour in 24 hour format
    public int getHour(){
        return this.localDateTime.getHour();
    }

    public int getMinute(){
        return this.localDateTime.getMinute();
    }

}
