package com.example.insight.utility;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.insight.receiver.AlarmReceiver;

import java.util.Calendar;

public class AlarmHelper {

    public static void setAlarm(Context context, int requestCode, Calendar calendar, String medicationId, String medicationName) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);

        intent.putExtra("medicationId", medicationId);
        intent.putExtra("medicationName", medicationName);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        long triggerTime = calendar.getTimeInMillis();

        Log.d("AlarmHelper", "🚀 setAlarm() called! Alarm set for: " + triggerTime);

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
    }
}
