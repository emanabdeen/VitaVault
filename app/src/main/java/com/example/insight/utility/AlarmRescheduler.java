package com.example.insight.utility;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.insight.model.MedicationAlarm;
import com.example.insight.receiver.AlarmReceiver;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AlarmRescheduler {

    public static void rescheduleAll(Context context) {
        Log.d("AlarmRescheduler", "Rescheduler called");
        List<MedicationAlarm> alarms = AlarmLocalStorageHelper.getAlarms(context);

        // Prevent duplicate rescheduling using a HashSet
        Set<String> seen = new HashSet<>();

        for (MedicationAlarm alarm : alarms) {

            String key = alarm.getMedicationId() + alarm.getDay() + alarm.getTime();

            // Skip if this alarm has already been processed
            if (!seen.add(key)) {
                continue;
            }

            int requestCode = AlarmStaticUtils.generateUniqueRequestCode(
                    alarm.getMedicationId(),
                    alarm.getDay(),
                    alarm.getTime()
            );

            // Cancel any previously existing alarm (safe, won't crash if not found)
            AlarmHelper.cancelAlarm(context, requestCode, alarm.getMedicationId(), alarm.getMedicationName());

            // Always re-schedule (ensures alarm actually exists in system)
            Calendar calendar = AlarmStaticUtils.getNextAlarmTime(alarm.getDay(), alarm.getTime());

            AlarmHelper.setAlarm(
                    context,
                    requestCode,
                    calendar,
                    alarm.getMedicationId(),
                    alarm.getMedicationName(),
                    true,
                    alarm.getDosage()
            );

            Log.d("AlarmRescheduler", "✅ Re-scheduled alarm for: " + alarm.getMedicationName() + " at " + alarm.getTime());
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
