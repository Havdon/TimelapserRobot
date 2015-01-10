package com.oskarkoli.timelapserandroid.util;

/**
 * Utility time class.
 */
public class TimeObject {
    public int milliseconds;

    public TimeObject(int hour, int minutes) {
        this(hour, minutes, 0);
    }

    public TimeObject(int hour, int minutes, int seconds) {
        assert(hour > 0 && hour <= 24);
        assert(minutes > 0 && minutes <= 59);
        assert(seconds > 0 && seconds <= 59);

        milliseconds = seconds * 1000;
        milliseconds += minutes * 60000;
        milliseconds += hour * 3600000;

        if (milliseconds == 0) {
            this.milliseconds = 0;
        }
    }

    public static TimeObject fromSeconds(int seconds) {
        return fromMilliseconds(seconds * 1000);
    }

    public static TimeObject fromMilliseconds(int ms) {
        TimeObject obj = new TimeObject(0, 0);
        obj.milliseconds = ms;
        return obj;
    }

    public int hours() { return (int) Math.floor(Math.floor((milliseconds / 1000f) / 60f) / 60f); }
    public int minutes() { return (int) Math.floor((milliseconds / 1000f) / 60f  % 60); }
    public int seconds() {
        return (int) (milliseconds / 1000f) % 60;
    }
    public int milliseconds() {
        return  milliseconds;
    }

    private String formatNumber(int num) {
        if(num < 10) {
            return "0" + num;
        }
        return "" + num;
    }

    @Override
    public String toString() {
        int ms = milliseconds;
        float seconds = ms / 1000f;
        int minutes = (int) Math.floor(seconds / 60f);
        int hours = (int) Math.floor(minutes / 60f);
        minutes = minutes % 60;
        seconds = seconds % 60;
        String retStr = "";
        if (hours > 0) {
            retStr += hours + " hr" + (hours > 1 ? "s" : "");
        }
        if (minutes > 0) {
            retStr += " " + minutes + " min" + (minutes > 1 ? "s" : "");
        }
        if (seconds > 0) {
            retStr += " " + seconds + " sec" + (seconds > 1 ? "s" : "");
        }
        return retStr;
    }
}