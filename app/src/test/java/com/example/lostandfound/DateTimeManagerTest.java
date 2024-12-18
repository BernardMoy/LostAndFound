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
    public void testToEpoch(){
        assertEquals(EPOCH, DateTimeManager.toEpoch(YEAR, MONTH, DAY, HOUR, MINUTE));
    }

    @Test
    public void testGetFormattedString(){
        assertEquals("18 Jun 2024 17:04", DateTimeManager.getFormattedDate(EPOCH));
    }
}
