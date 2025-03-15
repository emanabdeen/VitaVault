package com.example.insight.utility;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.insight.model.Medication;
import com.example.insight.model.MedicationAlarm;
import com.example.insight.receiver.AlarmReceiver;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class AlarmHelper {

    public static void setAlarm(Context context, int requestCode, Calendar calendar, String medicationId, String medicationName, boolean repeatWeekly, String dosage) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);

        intent.putExtra("medicationId", medicationId);
        intent.putExtra("medicationName", medicationName);
        intent.putExtra("repeatWeekly", repeatWeekly); // Pass it along to the receiver
        intent.putExtra("dosage", dosage);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        long triggerTime = calendar.getTimeInMillis();

        // Always use exact and allow while idle
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);

        if (repeatWeekly) {
            Log.d("AlarmHelper", "⏰ One-time alarm set, but will repeat weekly via rescheduling in receiver. Time: " + triggerTime);
        } else {
            Log.d("AlarmHelper", "⏰ One-time alarm set for: " + triggerTime);
        }
    }

    public static void cancelAlarm(Context context, int requestCode, String medicationId, String medicationName) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("medicationId", medicationId);
        intent.putExtra("medicationName", medicationName);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        alarmManager.cancel(pendingIntent); // ❌ Cancel alarm
        Log.d("AlarmHelper", "🛑 Alarm canceled for: " + medicationName);
    }

    public static void cancelAllAlarmsForMedication(Context context,
                                                    String medicationId,
                                                    String medicationName,
                                                    List<MedicationAlarm> alarmList) {
        for (MedicationAlarm alarm : alarmList) {
            // Recreate the same request code used when setting the alarm
            int requestCode = (medicationId + alarm.getDay() + alarm.getTime()).hashCode();
            cancelAlarm(context, requestCode, medicationId, medicationName);
        }
    }



}
