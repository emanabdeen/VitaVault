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

        String medicationId = intent.getStringExtra("medicationId");
        String dosage = intent.getStringExtra("dosage");

        // If user presses "DISMISS" button
        if ("STOP_ALARM".equals(action)) {
            Log.d("AlarmReceiver", "🚨 DISMISS clicked in notification");

            // Log "Dismissed" in Firestore
            logMedicationDismissed(context, medicationId, dosage);

            // Stop alarm sound and remove notification
            stopAlarm(context);
            return; // Stop further execution
        }

        // If normal alarm trigger
        String medicationName = intent.getStringExtra("medicationName");
        boolean repeatWeekly = intent.getBooleanExtra("repeatWeekly", false);


        Log.d("AlarmReceiver", "🚨 AlarmReceiver triggered for: " + medicationName);

        // Play alarm sound
        playAlarmSound(context);



        // Show persistent notification
        showPersistentNotification(context, medicationId, medicationName, dosage);

        if (repeatWeekly) {
            Calendar nextWeek = Calendar.getInstance();
            nextWeek.setTimeInMillis(System.currentTimeMillis());
            nextWeek.add(Calendar.WEEK_OF_YEAR, 1);

            int requestCode = (medicationId + medicationName).hashCode(); // Same way as when set

            AlarmHelper.setAlarm(context, requestCode, nextWeek, medicationId, medicationName, true, dosage);
            Log.d("AlarmReceiver", "🔁 Repeating weekly alarm re-scheduled for next week.");
        }
    }

    // Play alarm sound
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

    // Show notification with "DISMISS" button
    private void showPersistentNotification(Context context, String medicationId, String medicationName, String dosage) {
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
        openAppIntent.putExtra("dosage", dosage);
        openAppIntent.putExtra("medicationName", medicationName);
        openAppIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent openAppPendingIntent = PendingIntent.getActivity(
                context, medicationId.hashCode(), openAppIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // DISMISS button action
        Intent stopIntent = new Intent(context, AlarmReceiver.class);
        stopIntent.setAction("STOP_ALARM");
        stopIntent.putExtra("medicationId", medicationId);
        stopIntent.putExtra("dosage", dosage);
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(
                context, medicationId.hashCode(), stopIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );


        // Build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.icon_medications) // Ensure white transparent icon
                .setContentTitle("💊 Medication Reminder")
                .setContentText("Time to take: " + medicationName + ". Tap DISMISS to cancel.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setAutoCancel(false)
                .setOngoing(true)
                .addAction(android.R.drawable.ic_menu_close_clear_cancel, "DISMISS", stopPendingIntent) // Button renamed
                .setContentIntent(openAppPendingIntent)
                .setFullScreenIntent(openAppPendingIntent, true); // Optional heads-up popup

        // Show notification
        Log.d("AlarmReceiver", "🚀 Ready to show notification...");
        notificationManager.notify(NOTIFICATION_ID, builder.build());
        Log.d("AlarmReceiver", "✅ Notification displayed!");
    }

    // Log Dismiss action to FireStore
    private void logMedicationDismissed(Context context, String medicationId, String dosage) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> log = new HashMap<>();
        log.put("medicationId", medicationId);
        log.put("status", "Dismissed"); // Status for dismiss
        log.put("timestamp", FieldValue.serverTimestamp());
        log.put("dosage", dosage);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getCurrentUser().getUid();

        db.collection("users").document(uid).collection("medications")
                .document(medicationId).collection("logs").add(log)
                .addOnSuccessListener(doc -> Log.d("AlarmReceiver", "✅ Dismiss log added"))
                .addOnFailureListener(e -> Log.e("AlarmReceiver", "❌ Failed to log dismissal", e));
    }

    // Stop alarm sound and remove notification
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
