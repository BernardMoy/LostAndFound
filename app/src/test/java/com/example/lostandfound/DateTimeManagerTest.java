package com.example.lostandfound;

import static org.junit.Assert.assertEquals;

import com.example.lostandfound.Utility.DateTimeManager;

import org.junit.Test;

public class DateTimeManagerTest {
    private static final int YEAR = 2024;
    private static final int MONTH = 6;
    private static final int DAY = 18;
    private static final int HOUR = 17;
    private static final int MINUTE = 4;
    private static final long EPOCH = 1718730240L;

    @Test
    public void testGetEpoch(){
        DateTimeManager dateTimeManager1 = new DateTimeManager(YEAR, MONTH, DAY, HOUR, MINUTE);
        assertEquals(EPOCH, dateTimeManager1.getEpoch());

        DateTimeManager dateTimeManager2 = new DateTimeManager(EPOCH);
        assertEquals(EPOCH, dateTimeManager2.getEpoch());
    }

    @Test
    public void testGetFormattedString(){
        DateTimeManager dateTimeManager1 = new DateTimeManager(YEAR, MONTH, DAY, HOUR, MINUTE);
        assertEquals("18 Jun 2024", dateTimeManager1.getFormattedDate());

        DateTimeManager dateTimeManager2 = new DateTimeManager(EPOCH);
        assertEquals("17:04", dateTimeManager2.getFormattedTime());
    }

    @Test
    public void testGetYear(){
        DateTimeManager dateTimeManager1 = new DateTimeManager(YEAR, MONTH, DAY, HOUR, MINUTE);
        assertEquals(YEAR, dateTimeManager1.getYear());

        DateTimeManager dateTimeManager2 = new DateTimeManager(EPOCH);
        assertEquals(YEAR, dateTimeManager2.getYear());
    }

    @Test
    public void testGetMonth(){
        DateTimeManager dateTimeManager1 = new DateTimeManager(YEAR, MONTH, DAY, HOUR, MINUTE);
        assertEquals(MONTH, dateTimeManager1.getMonth());

        DateTimeManager dateTimeManager2 = new DateTimeManager(EPOCH);
        assertEquals(MONTH, dateTimeManager2.getMonth());
    }

    @Test
    public void testGetDay(){
        DateTimeManager dateTimeManager1 = new DateTimeManager(YEAR, MONTH, DAY, HOUR, MINUTE);
        assertEquals(DAY, dateTimeManager1.getDay());

        DateTimeManager dateTimeManager2 = new DateTimeManager(EPOCH);
        assertEquals(DAY, dateTimeManager2.getDay());
    }

    @Test
    public void testGetHour(){
        DateTimeManager dateTimeManager1 = new DateTimeManager(YEAR, MONTH, DAY, HOUR, MINUTE);
        assertEquals(HOUR, dateTimeManager1.getHour());

        DateTimeManager dateTimeManager2 = new DateTimeManager(EPOCH);
        assertEquals(HOUR, dateTimeManager2.getHour());
    }

    @Test
    public void testGetMinute(){
        DateTimeManager dateTimeManager1 = new DateTimeManager(YEAR, MONTH, DAY, HOUR, MINUTE);
        assertEquals(MINUTE, dateTimeManager1.getMinute());

        DateTimeManager dateTimeManager2 = new DateTimeManager(EPOCH);
        assertEquals(MINUTE, dateTimeManager2.getMinute());
    }

}
