package com.example.insight.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.insight.utility.AlarmHelper;
import com.example.insight.utility.AlarmLocalStorageHelper;
import com.example.insight.utility.AlarmRescheduler;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
//        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
//            Log.d("BootReceiver", "Device rebooted – re-scheduling alarms...");
//            // Re-schedule alarms from local storage
//            AlarmRescheduler.rescheduleAll(context);
//        }
        String action = intent.getAction();
        if (Intent.ACTION_BOOT_COMPLETED.equals(action) || "com.example.insight.TEST_BOOT".equals(action)) {
            Log.d("BootReceiver", "✅ BOOT completed or test intent received – re-scheduling alarms...");
            AlarmRescheduler.rescheduleAll(context);
        }
    }
}

