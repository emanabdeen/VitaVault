package com.example.insight.utility;


import android.content.Context;
import android.util.Log;
import com.example.insight.model.MedicationAlarm;
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
}
