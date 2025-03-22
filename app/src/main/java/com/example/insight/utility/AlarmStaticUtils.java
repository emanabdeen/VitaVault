package com.example.insight.utility;

import java.util.Calendar;

public class AlarmStaticUtils {

    // Convert day string to index for spinner
    public static int getDayIndex(String day) {
        switch (day) {
            case "Monday": return 0;
            case "Tuesday": return 1;
            case "Wednesday": return 2;
            case "Thursday": return 3;
            case "Friday": return 4;
            case "Saturday": return 5;
            case "Sunday": return 6;
            default: return 0;
        }
    }

    // Get Calendar constant for day of week
    public static int getDayOfWeek(String day) {
        switch (day) {
            case "Sunday": return Calendar.SUNDAY;
            case "Monday": return Calendar.MONDAY;
            case "Tuesday": return Calendar.TUESDAY;
            case "Wednesday": return Calendar.WEDNESDAY;
            case "Thursday": return Calendar.THURSDAY;
            case "Friday": return Calendar.FRIDAY;
            case "Saturday": return Calendar.SATURDAY;
            default: return Calendar.MONDAY;
        }
    }

    // Parse hour from "hh:mm AM/PM" format
    public static int getHour(String time) {
        String[] parts = time.split("[: ]");
        int hour = Integer.parseInt(parts[0]);
        if (parts[2].equals("PM") && hour != 12) hour += 12;
        if (parts[2].equals("AM") && hour == 12) hour = 0;
        return hour;
    }

    // Parse minute from "hh:mm AM/PM" format
    public static int getMinute(String time) {
        return Integer.parseInt(time.split("[: ]")[1]);
    }

    // Generate unique request code based on medication and alarm details
    public static int generateUniqueRequestCode(String medicationId, String day, String time) {
        return (medicationId + day + time).hashCode();
    }

    // Get next alarm time Calendar object
    public static Calendar getNextAlarmTime(String day, String time) {
        Calendar calendar = Calendar.getInstance();
        String[] timeParts = time.split(" ");
        String[] hourMin = timeParts[0].split(":");
        int hour = Integer.parseInt(hourMin[0]);
        int minute = Integer.parseInt(hourMin[1]);
        String amPm = timeParts[1];
        if (amPm.equals("PM") && hour != 12) hour += 12;
        if (amPm.equals("AM") && hour == 12) hour = 0;
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.DAY_OF_WEEK, getDayOfWeek(day));
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.WEEK_OF_YEAR, 1);
        }
        return calendar;
    }
}

