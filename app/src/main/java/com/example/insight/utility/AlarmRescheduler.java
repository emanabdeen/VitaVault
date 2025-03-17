package com.example.insight.utility;

import android.content.Context;
import android.util.Log;

import com.example.insight.model.MedicationAlarm;

import java.util.Calendar;
import java.util.List;

public class AlarmRescheduler {

    public static void rescheduleAll(Context context) {
        List<MedicationAlarm> alarms = AlarmLocalStorageHelper.getAlarms(context);
        for (MedicationAlarm alarm : alarms) {
            // Convert day and time to a Calendar object.
            Calendar calendar = getNextAlarmTime(alarm.getDay(), alarm.getTime());
            int requestCode = (alarm.getMedicationId() + alarm.getDay() + alarm.getTime()).hashCode();

            // Re-schedule the alarm using your AlarmHelper
            AlarmHelper.setAlarm(
                    context,
                    requestCode,
                    calendar,
                    alarm.getMedicationId(),
                    alarm.getMedicationName(),
                    true,
                    alarm.getDosage()
            );

            Log.d("AlarmRescheduler", "Re-scheduled alarm for: " + alarm.getMedicationName());
        }
    }

    // Helper method to convert a day and time string (e.g., "Monday", "08:00 AM") into a Calendar object.
    private static Calendar getNextAlarmTime(String day, String time) {
        Calendar calendar = Calendar.getInstance();
        // Assume time is in the format "08:00 AM"
        String[] timeParts = time.split(" ");
        String[] hourMin = timeParts[0].split(":");
        int hour = Integer.parseInt(hourMin[0]);
        int minute = Integer.parseInt(hourMin[1]);
        String amPm = timeParts[1];

        if (amPm.equalsIgnoreCase("PM") && hour != 12) {
            hour += 12;
        }
        if (amPm.equalsIgnoreCase("AM") && hour == 12) {
            hour = 0;
        }
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

    // Convert a day name to a Calendar constant.
    private static int getDayOfWeek(String day) {
        return switch (day) {
            case "Sunday" -> Calendar.SUNDAY;
            case "Monday" -> Calendar.MONDAY;
            case "Tuesday" -> Calendar.TUESDAY;
            case "Wednesday" -> Calendar.WEDNESDAY;
            case "Thursday" -> Calendar.THURSDAY;
            case "Friday" -> Calendar.FRIDAY;
            case "Saturday" -> Calendar.SATURDAY;
            default -> Calendar.MONDAY;
        };
    }
}
