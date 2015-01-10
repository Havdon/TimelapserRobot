package com.oskarkoli.timelapserandroid.tests;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.oskarkoli.timelapserandroid.util.TimeObject;

/**
 * Unit tests for TimeObject
 */
public class TimeObjectTest extends AndroidTestCase {

    private final static int SECOND_TO_MS = 1000;
    private final static int HOUR_TO_MS = 3600000;

    public TimeObjectTest() {
        super();
    }

    @SmallTest
    public void testTimeObjectToMilliseconds() {
        TimeObject obj;

        // n second = (1000 * n) ms
        for(int i = 0; i < 60; i++) {
            obj = new TimeObject(0, i);
            assertEquals(i * SECOND_TO_MS, obj.milliseconds());
        }

        // n hour = (3600000 * n) ms
        for(int i = 0; i < 12; i++) {
            obj = new TimeObject(i, 0);
            assertEquals(i * HOUR_TO_MS, obj.milliseconds());
        }

        // n hour = (3600000 * n) ms

        for(int j = 0; j < 60; j++) {
            for (int i = 0; i < 12; i++) {
                obj = new TimeObject(i, j);
                assertEquals((i * HOUR_TO_MS + j * SECOND_TO_MS), obj.milliseconds());
            }
        }
    }



}
