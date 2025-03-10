package com.example.insight.utility;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

public class AlarmSoundHelper {
    private static Ringtone ringtone;

    public static void playAlarmSound(Context context) {
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }

        ringtone = RingtoneManager.getRingtone(context, alarmUri);
        ringtone.play();
    }

    public static void stopAlarmSound() {
        if (ringtone != null && ringtone.isPlaying()) ringtone.stop();
    }
}
