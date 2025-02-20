package com.example.lostandfound.Utility;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class DateTimeManager {
    private DateTimeManager() {
    }

    // method to get the current hour and minute
    public static int getCurrentHour() {
        LocalTime currentTime = LocalTime.now();
        return currentTime.getHour();
    }

    public static int getCurrentMinute() {
        LocalTime currentTime = LocalTime.now();
        return currentTime.getMinute();
    }

    // for storing the current epoch time into the database to retrieve them in order
    public static long getCurrentEpochTime() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        ZonedDateTime zonedCurrentDateTime = currentDateTime.atZone(ZoneId.of("UTC"));
        return zonedCurrentDateTime.toEpochSecond();
    }

    // for getting the current epoch time but at midnight (00:00)
    public static long getCurrentEpochDate() {
        LocalDateTime currentDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
        ZonedDateTime zonedCurrentDateTime = currentDateTime.atZone(ZoneId.of("UTC"));
        return zonedCurrentDateTime.toEpochSecond();
    }

    // the date is stored in epoch in seconds
    public static String dateToString(long epochSeconds) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(epochSeconds),
                ZoneId.of("UTC")
        );

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        return formatter.format(localDateTime);
    }

    // the time is stored in seconds
    public static String timeToString(int hour, int minute) {
        return String.format(Locale.UK, "%02d:%02d", hour, minute);
    }

    // get the date and time both to string when given a date time epoch
    public static String dateTimeToString(long epoch) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(epoch),
                ZoneId.of("UTC")
        );

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
        return formatter.format(localDateTime);
    }


    // get the epoch stored in the database: date epoch + time seconds
    public static long getDateTimeEpoch(long dateEpochSeconds, int hour, int minute) {
        long epochTime = (long) hour * 60 * 60 + minute * 60L;
        return dateEpochSeconds + epochTime;
    }

    // method to check whether an epoch datetime is greater than the current time
    public static boolean isTimeInFuture(long dateTimeEpochSeconds) {
        long currentEpoch = getCurrentEpochTime();

        // check if the given time is greater than the current time
        return dateTimeEpochSeconds > currentEpoch;
    }

    // method to get the date epoch from a given epoch time stored in db
    public static long epochToDate(long epoch) {
        LocalDate localDate = Instant.ofEpochSecond(epoch).atZone(ZoneId.of("UTC")).toLocalDate();
        return localDate.atStartOfDay(ZoneId.of("UTC")).toEpochSecond();
    }

    // method to get the hour from a given epoch time
    public static int epochToHour(long epoch) {
        LocalDateTime localDateTime = Instant.ofEpochSecond(epoch).atZone(ZoneId.of("UTC")).toLocalDateTime();
        return localDateTime.getHour();
    }

    // method to get the minute from a given epoch time
    public static int epochToMinute(long epoch) {
        LocalDateTime localDateTime = Instant.ofEpochSecond(epoch).atZone(ZoneId.of("UTC")).toLocalDateTime();
        return localDateTime.getMinute();
    }

    // method to get the string describing when the item is posted
    // either last hour, n hours ago or n days ago
    public static String getDescription(long epoch) {
        long currentEpoch = getCurrentEpochTime();

        // within last hour
        if (currentEpoch - epoch < 3600) {
            return "Last hour";

        } else {
            // calculate the number of hours elapsed
            int numberOfHours = (int) ((currentEpoch - epoch) / 3600);   // scale it down
            if (numberOfHours == 1) {
                return numberOfHours + " hour ago";

            } else if (numberOfHours < 24) {
                return numberOfHours + " hours ago";

            } else {
                // calculate the number of days elapsed
                int numberOfDays = (int) ((currentEpoch - epoch) / 86400);
                if (numberOfDays == 1) {
                    return "a day ago";

                } else {
                    return numberOfDays + " days ago";

                }
            }
        }
    }

    // method to get the string describing a chat message
    // the time is shown if within last day, otherwise it shows N days ago
    public static String getChatTimeDescription(long epoch) {
        long currentEpoch = getCurrentEpochTime();

        if (currentEpoch - epoch < 86400) {
            // within last day, return the hour minute
            LocalDateTime localDateTime = Instant.ofEpochSecond(epoch).atZone(ZoneId.of("UTC")).toLocalDateTime();
            return timeToString(localDateTime.getHour(), localDateTime.getMinute());

        } else {
            // return N days ago
            int numberOfDays = (int) ((currentEpoch - epoch) / 86400);
            if (numberOfDays == 1) {
                return "a day ago";

            } else {
                return numberOfDays + " days ago";

            }
        }
    }
}
