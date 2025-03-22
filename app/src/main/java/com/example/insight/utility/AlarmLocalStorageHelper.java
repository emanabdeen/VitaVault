package com.example.insight.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.insight.model.MedicationAlarm;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AlarmLocalStorageHelper {
    private static final String PREFS_NAME = "AlarmPrefs";
    private static final String KEY_ALARMS = "alarms";

    // Save the list of alarms to SharedPreferences
    public static void saveAlarms(Context context, List<MedicationAlarm> alarms) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String json = new Gson().toJson(alarms);
        editor.putString(KEY_ALARMS, json);
        editor.apply();
    }

    // Retrieve the list of alarms from SharedPreferences
    public static List<MedicationAlarm> getAlarms(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_ALARMS, "[]");
        Type listType = new TypeToken<ArrayList<MedicationAlarm>>() {}.getType();
        return new Gson().fromJson(json, listType);
    }

    // Remove an alarm that matches the provided MedicationAlarm
    public static void removeAlarm(Context context, MedicationAlarm alarmToRemove) {
        List<MedicationAlarm> alarms = getAlarms(context);
        Iterator<MedicationAlarm> iterator = alarms.iterator();
        while (iterator.hasNext()) {
            MedicationAlarm alarm = iterator.next();
            // Adjust the matching criteria as needed (using medicationId, day, time, dosage)
            if (alarm.getMedicationId().equals(alarmToRemove.getMedicationId()) &&
                    alarm.getDay().equals(alarmToRemove.getDay()) &&
                    alarm.getTime().equals(alarmToRemove.getTime()) &&
                    alarm.getDosage().equals(alarmToRemove.getDosage())) {
                iterator.remove();
                Log.d("AlarmLocalStorageHelper", "Removed alarm for: " + alarm.getMedicationName());
            }
        }
        saveAlarms(context, alarms);
    }

    // Wipes ALL alarms from local storage
    public static void clearAll(Context context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .remove(KEY_ALARMS)
                .apply();
        Log.d("AlarmLocalStorageHelper", "✅ All stored alarms cleared.");
    }
}
