package com.example.insight.receiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.insight.R;
import com.example.insight.utility.AlarmHelper;
import com.example.insight.view.AlarmScreenActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AlarmReceiver extends BroadcastReceiver {
    private static final int NOTIFICATION_ID = 1001;
    private static final String CHANNEL_ID = "alarm_channel";
    static Ringtone ringtone;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        // ✅ If user presses "DISMISS" button
        if ("STOP_ALARM".equals(action)) {
            Log.d("AlarmReceiver", "🚨 DISMISS clicked in notification");

            // Log "Dismissed" in Firestore
            String medicationId = intent.getStringExtra("medicationId");
            logMedicationDismissed(context, medicationId);

            // Stop alarm sound and remove notification
            stopAlarm(context);
            return; // Stop further execution
        }

        // ✅ If normal alarm trigger
        String medicationId = intent.getStringExtra("medicationId");
        String medicationName = intent.getStringExtra("medicationName");
        boolean repeatWeekly = intent.getBooleanExtra("repeatWeekly", false);

        Log.d("AlarmReceiver", "🚨 AlarmReceiver triggered for: " + medicationName);

        // Play alarm sound
        playAlarmSound(context);



        // Show persistent notification
        showPersistentNotification(context, medicationId, medicationName);

        if (repeatWeekly) {
            Calendar nextWeek = Calendar.getInstance();
            nextWeek.setTimeInMillis(System.currentTimeMillis());
            nextWeek.add(Calendar.WEEK_OF_YEAR, 1);

            int requestCode = (medicationId + medicationName).hashCode(); // Same way as when set

            AlarmHelper.setAlarm(context, requestCode, nextWeek, medicationId, medicationName, true);
            Log.d("AlarmReceiver", "🔁 Repeating weekly alarm re-scheduled for next week.");
        }
    }

    // ✅ Play alarm sound
    private void playAlarmSound(Context context) {
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }

        ringtone = RingtoneManager.getRingtone(context, alarmUri);
        if (ringtone != null) {
            ringtone.play();
            Log.d("AlarmReceiver", "✅ Alarm sound is playing...");
        } else {
            Log.e("AlarmReceiver", "❌ Failed to play alarm sound!");
        }
    }

    // ✅ Show notification with "DISMISS" button
    private void showPersistentNotification(Context context, String medicationId, String medicationName) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create Notification Channel (Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Medication Reminders",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Reminders for taking medications");
            channel.enableVibration(true);
            channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(channel);
        }

        Intent openAppIntent = new Intent(context, AlarmScreenActivity.class);
        openAppIntent.putExtra("medicationId", medicationId);
        openAppIntent.putExtra("medicationName", medicationName);
        openAppIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent openAppPendingIntent = PendingIntent.getActivity(
                context, medicationId.hashCode(), openAppIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // ✅ DISMISS button action
        Intent stopIntent = new Intent(context, AlarmReceiver.class);
        stopIntent.setAction("STOP_ALARM");
        stopIntent.putExtra("medicationId", medicationId); // Pass medication ID for logging
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(
                context, medicationId.hashCode(), stopIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // ✅ Tap notification to open AlarmScreenActivity


        // ✅ Build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_alarm) // Ensure white transparent icon
                .setContentTitle("💊 Medication Reminder")
                .setContentText("Time to take: " + medicationName + ". Tap DISMISS to cancel.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setAutoCancel(false)
                .setOngoing(true)
                .addAction(android.R.drawable.ic_menu_close_clear_cancel, "DISMISS", stopPendingIntent) // ✅ Button renamed
                .setContentIntent(openAppPendingIntent)
                .setFullScreenIntent(openAppPendingIntent, true); // Optional heads-up popup

        // ✅ Show notification
        Log.d("AlarmReceiver", "🚀 Ready to show notification...");
        notificationManager.notify(NOTIFICATION_ID, builder.build());
        Log.d("AlarmReceiver", "✅ Notification displayed!");
    }

    // ✅ Log Dismiss action to Firestore
    private void logMedicationDismissed(Context context, String medicationId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> log = new HashMap<>();
        log.put("medicationId", medicationId);
        log.put("status", "Dismissed"); // Status for dismiss
        log.put("timestamp", FieldValue.serverTimestamp());

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getCurrentUser().getUid();

        db.collection("users").document(uid).collection("medications")
                .document(medicationId).collection("logs").add(log)
                .addOnSuccessListener(doc -> Log.d("AlarmReceiver", "✅ Dismiss log added"))
                .addOnFailureListener(e -> Log.e("AlarmReceiver", "❌ Failed to log dismissal", e));
    }

    // ✅ Stop alarm sound and remove notification
    public static void stopAlarm(Context context) {
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
            Log.d("AlarmReceiver", "✅ Alarm sound stopped");
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancel(NOTIFICATION_ID);
        }
    }
}
