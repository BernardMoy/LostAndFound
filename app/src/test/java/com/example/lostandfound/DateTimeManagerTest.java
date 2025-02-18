package com.example.lostandfound;

import static com.example.lostandfound.Utility.DateTimeManager.getCurrentEpochTime;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.example.lostandfound.Utility.DateTimeManager;

import org.junit.Test;

public class DateTimeManagerTest {
    private static final int YEAR = 2024;
    private static final int MONTH = 6;
    private static final int DAY = 18;
    private static final int HOUR = 17;
    private static final int MINUTE = 4;
    private static final long EPOCH_DATE_ONLY = 1718668800L;
    private static final int TIME_SECONDS = 61440;
    private static final long EPOCH = 1718730240L;

    @Test
    public void testDateToString(){
        assertEquals("18 Jun 2024", DateTimeManager.dateToString(EPOCH_DATE_ONLY));
    }

    @Test
    public void testTimeToString(){
        assertEquals("17:04", DateTimeManager.timeToString(HOUR, MINUTE));
    }

    @Test
    public void testGetDateTimeEpoch(){
        assertEquals(EPOCH, DateTimeManager.getDateTimeEpoch(EPOCH_DATE_ONLY, HOUR, MINUTE));
    }

    @Test
    public void testEpochToDate(){
        assertEquals(EPOCH_DATE_ONLY, DateTimeManager.epochToDate(EPOCH));
    }

    @Test
    public void testEpochToHour(){
        assertEquals(HOUR, DateTimeManager.epochToHour(EPOCH));
    }

    @Test
    public void testEpochToMinute(){
        assertEquals(MINUTE, DateTimeManager.epochToMinute(EPOCH));
    }

    @Test
    public void testDateTimeToString(){
        assertEquals("18 Jun 2024 17:04", DateTimeManager.dateTimeToString(EPOCH));
    }

    @Test
    public void testGetDescription(){
        long currentEpoch = getCurrentEpochTime();
        assertEquals("2 days ago", DateTimeManager.getDescription(currentEpoch - 86400*2-300));
    }

    @Test
    public void testGetChatTimeDescription(){
        long currentEpoch = getCurrentEpochTime();
        assertEquals("a day ago", DateTimeManager.getChatTimeDescription(currentEpoch - 86400*1-300));
    }
}
