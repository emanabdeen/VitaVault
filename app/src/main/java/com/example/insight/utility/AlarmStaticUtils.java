package com.example.insight.utility;

import java.util.Calendar;

public class AlarmStaticUtils {

    // Convert day string to index for spinner
    public static int getDayIndex(String day) {
        if (day == null) return 0;

        switch (day.trim().toLowerCase()) {
            case "monday": return 0;
            case "tuesday": return 1;
            case "wednesday": return 2;
            case "thursday": return 3;
            case "friday": return 4;
            case "saturday": return 5;
            case "sunday": return 6;
            default: return 0;
        }
    }

    // Get Calendar constant for day of week
    public static int getDayOfWeek(String day) {

        switch (day.trim().toLowerCase()) {
            case "sunday": return Calendar.SUNDAY;
            case "monday": return Calendar.MONDAY;
            case "tuesday": return Calendar.TUESDAY;
            case "wednesday": return Calendar.WEDNESDAY;
            case "thursday": return Calendar.THURSDAY;
            case "friday": return Calendar.FRIDAY;
            case "saturday": return Calendar.SATURDAY;
            default: return Calendar.MONDAY;
        }
    }

    // Parse hour from "HH:mm" format (24-hour)
    public static int getHour(String time) {
        return Integer.parseInt(time.split(":")[0]);
    }

    // Parse minute from "HH:mm" format (24-hour)
    public static int getMinute(String time) {
        return Integer.parseInt(time.split(":")[1]);
    }

    // Generate unique request code based on medication and alarm details
    public static int generateUniqueRequestCode(String medicationId, String day, String time) {
        return (medicationId + day + time).hashCode();
    }

    // Get next alarm time Calendar object from "HH:mm" format
    public static Calendar getNextAlarmTime(String day, String time) {
        Calendar calendar = Calendar.getInstance();

        int hour = getHour(time);
        int minute = getMinute(time);

        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.DAY_OF_WEEK, getDayOfWeek(day));

        // If the scheduled time has already passed this week, push to next week
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.WEEK_OF_YEAR, 1);
        }

        return calendar;
    }
}

